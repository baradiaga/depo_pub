import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

// Importation des services
import { FormationService } from '../../../../services/formation.service';
import { ElementConstitutifService } from '../../../../services/element-constitutif.service';
import { EtablissementService, EtablissementSearchParams } from '../../../../services/etablissement.service';
import { DepartementService, DepartementSearchParams } from '../../../../services/departement.service';
import { UefrService, UefrSearchParams } from '../../../../services/uefr.service';

// Importation des interfaces
import {
  FormationCreation,
  FormationDetail,
  ElementConstitutifResponse,
  StatutFormation,
  NiveauEtude,
  ModaliteEnseignement,
  NiveauAcquisition,
  UniteEnseignement,
  CompetenceDetail,
  Etablissement,
  Uefr,
  Departement
} from '../../../../models/models';

@Component({
  selector: 'app-gestion-formations',
  templateUrl: './gestion-formations.component.html',
  styleUrls: ['./gestion-formations.component.css']
})
export class GestionFormationsComponent implements OnInit {

  formations: FormationDetail[] = [];
  isLoading = false;

  // Données pour les nouveaux champs
  etablissements: Etablissement[] = [];
  uefrs: Uefr[] = [];
  departements: Departement[] = [];
  
  // Pour le chargement des données dépendantes
  isLoadingEtablissements = false;
  isLoadingUefrs = false;
  isLoadingDepartements = false;

  // Constantes mises à jour
  tousLesElementsConstitutifs: ElementConstitutifResponse[] = [];
  statuts: StatutFormation[] = ['ACTIF', 'ARCHIVE', 'EN_PREPARATION', 'EN_VALIDATION'];
  niveaux: NiveauEtude[] = ['LICENCE', 'MASTER', 'CERTIFICAT', 'DOCTORAT', 'BACHELOR', 'MS'];
  modalites: ModaliteEnseignement[] = ['PRESENTIEL', 'DISTANCIEL', 'HYBRIDE'];
  niveauxAcquisition: NiveauAcquisition[] = ['INITIATION', 'MAITRISE', 'EXPERTISE'];

  anneesDisponibles: { value: number, label: string }[] = [];

  formationForm: FormGroup;
  editingFormationId: number | null = null;

  // Messages d'erreur
  errorMessageEtablissements = '';
  errorMessageUefrs = '';
  errorMessageDepartements = '';

  constructor(
    private fb: FormBuilder,
    private formationService: FormationService,
    private elementConstitutifService: ElementConstitutifService,
    private etablissementService: EtablissementService,
    private uefrService: UefrService,
    private departementService: DepartementService,
    private route: ActivatedRoute
  ) {
    this.formationForm = this.fb.group({
      // Nouveaux champs ajoutés
      etablissementId: ['', Validators.required],
      uefrId: ['', Validators.required],
      departementId: ['', Validators.required],
      
      // Champs existants
      nom: ['', [Validators.required, Validators.maxLength(200)]],
      code: ['', [Validators.required, Validators.maxLength(50)]],
      description: [''],
      statut: ['EN_PREPARATION', Validators.required],
      duree: [1, [Validators.required, Validators.min(1)]],
      niveauEtude: ['LICENCE', Validators.required],
      anneeCycle: [1, Validators.required],
      responsableId: [null],
      objectifs: [''],
      prerequis: [''],
      debouches: [''],
      evaluationModalites: [''],
      modaliteEnseignement: ['PRESENTIEL', Validators.required],
      lieu: [''],
      dateDebut: [''],
      dateFin: [''],
      capacite: [null],
      tarif: [null],
      certificationProfessionnelle: [''],
      intervenantsIds: [[]],
      documentsIds: [[]],
      // FormArray pour les structures imbriquées
      competences: this.fb.array([]),
      unitesEnseignement: this.fb.array([])
    });
  }

  ngOnInit(): void {
    this.chargerFormations();
    this.chargerElementsConstitutifs();
    this.chargerEtablissements();

    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.formationService.getFormationById(+id).subscribe({
        next: (formation) => this.editerFormation(formation),
        error: (err) => console.error(err)
      });
    }

    this.formationForm.get('niveauEtude')?.valueChanges.subscribe(val => {
      this.updateAnneesDisponibles(val);
    });
    this.updateAnneesDisponibles(this.formationForm.get('niveauEtude')?.value);
    
    // Écouter les changements sur l'établissement
    this.formationForm.get('etablissementId')?.valueChanges.subscribe(val => {
      this.onEtablissementChange(val);
    });
    
    // Écouter les changements sur l'UEFR
    this.formationForm.get('uefrId')?.valueChanges.subscribe(val => {
      this.onUefrChange(val);
    });
  }

  // ----------------------- Getters pour FormArray -----------------------

  get competences(): FormArray {
    return this.formationForm.get('competences') as FormArray;
  }

  get unitesEnseignement(): FormArray {
    return this.formationForm.get('unitesEnseignement') as FormArray;
  }

  // ----------------------- Gestion des nouveaux champs -----------------------

  chargerEtablissements(): void {
    this.isLoadingEtablissements = true;
    this.errorMessageEtablissements = '';
    
    // Essayons d'abord getAll() qui semble fonctionner
    this.etablissementService.getAll().subscribe({
      next: (data) => {
        this.etablissements = data;
        this.isLoadingEtablissements = false;
        console.log('Établissements chargés via getAll():', data.length);
      },
      error: (err) => {
        console.error('Erreur getAll() établissements:', err);
        
        // Tentative avec search sans paramètres
        const searchParams: EtablissementSearchParams = {};
        this.etablissementService.search(searchParams).subscribe({
          next: (result) => {
            this.etablissements = result.content || [];
            this.isLoadingEtablissements = false;
            console.log('Établissements chargés via search():', this.etablissements.length);
          },
          error: (err2) => {
            console.error('Erreur search() établissements:', err2);
            this.isLoadingEtablissements = false;
            this.errorMessageEtablissements = 'Impossible de charger les établissements. Veuillez réessayer.';
          }
        });
      }
    });
  }

  onEtablissementChange(etablissementId: any): void {
    if (!etablissementId) {
      this.resetUefrEtDepartement();
      return;
    }
    
    this.isLoadingUefrs = true;
    this.errorMessageUefrs = '';
    this.formationForm.get('uefrId')?.disable();
    this.formationForm.get('uefrId')?.reset();
    
    console.log('Chargement UEFR pour établissement ID:', etablissementId);
    
    // ESSAI 1: Avec search mais paramètres minimaux
    const searchParams: UefrSearchParams = {
      etablissementId: etablissementId
      // Pas d'autres paramètres pour éviter les erreurs
    };
    
    this.uefrService.search(searchParams).subscribe({
      next: (result) => {
        this.uefrs = result.content || [];
        this.isLoadingUefrs = false;
        this.formationForm.get('uefrId')?.enable();
        console.log('UEFR chargées via search():', this.uefrs.length);
        
        // Réinitialiser les départements
        this.resetDepartement();
      },
      error: (err) => {
        console.error('Erreur search() UEFR, tentative avec getAll():', err);
        
        // ESSAI 2: Charger toutes les UEFR et filtrer côté client
        this.uefrService.getAll().subscribe({
          next: (allUefrs) => {
            // Filtrer côté client
            this.uefrs = allUefrs.filter(u => u.etablissementId == etablissementId);
            this.isLoadingUefrs = false;
            this.formationForm.get('uefrId')?.enable();
            console.log('UEFR filtrées côté client:', this.uefrs.length);
            
            this.resetDepartement();
          },
          error: (err2) => {
            console.error('Erreur getAll() UEFR:', err2);
            this.isLoadingUefrs = false;
            this.formationForm.get('uefrId')?.enable();
            this.errorMessageUefrs = 'Impossible de charger les UFR pour cet établissement.';
            this.resetDepartement();
          }
        });
      }
    });
  }

  onUefrChange(uefrId: any): void {
    if (!uefrId) {
      this.resetDepartement();
      return;
    }
    
    this.isLoadingDepartements = true;
    this.errorMessageDepartements = '';
    this.formationForm.get('departementId')?.disable();
    this.formationForm.get('departementId')?.reset();
    
    console.log('Chargement départements pour UEFR ID:', uefrId);
    
    // ESSAI 1: Avec search paramètres minimaux
    const searchParams: DepartementSearchParams = {
      uefrId: uefrId
    };
    
    this.departementService.search(searchParams).subscribe({
      next: (result) => {
        this.departements = result.content || [];
        this.isLoadingDepartements = false;
        this.formationForm.get('departementId')?.enable();
        console.log('Départements chargés via search():', this.departements.length);
      },
      error: (err) => {
        console.error('Erreur search() départements, tentative avec getAll():', err);
        
        // ESSAI 2: Charger tous les départements et filtrer côté client
        this.departementService.getAll().subscribe({
          next: (allDepts) => {
            // Filtrer côté client
            this.departements = allDepts.filter(d => d.uefrId == uefrId);
            this.isLoadingDepartements = false;
            this.formationForm.get('departementId')?.enable();
            console.log('Départements filtrés côté client:', this.departements.length);
          },
          error: (err2) => {
            console.error('Erreur getAll() départements:', err2);
            this.isLoadingDepartements = false;
            this.formationForm.get('departementId')?.enable();
            this.errorMessageDepartements = 'Impossible de charger les départements pour cette UFR.';
          }
        });
      }
    });
  }

  // Méthodes utilitaires
  private resetUefrEtDepartement(): void {
    this.uefrs = [];
    this.departements = [];
    this.formationForm.get('uefrId')?.reset();
    this.formationForm.get('departementId')?.reset();
    this.formationForm.get('uefrId')?.disable();
    this.formationForm.get('departementId')?.disable();
    this.errorMessageUefrs = '';
    this.errorMessageDepartements = '';
  }

  private resetDepartement(): void {
    this.departements = [];
    this.formationForm.get('departementId')?.reset();
    this.formationForm.get('departementId')?.disable();
    this.errorMessageDepartements = '';
  }

  // ----------------------- Gestion Compétences -----------------------

  creerCompetenceFormGroup(competence: Partial<CompetenceDetail> = {}): FormGroup {
    return this.fb.group({
      libelle: [competence.libelle || '', Validators.required],
      niveauAcquisition: [competence.niveauAcquisition || 'INITIATION', Validators.required],
      indicateursEvaluation: [competence.indicateursEvaluation || '']
    });
  }

  ajouterCompetence(competence?: Partial<CompetenceDetail>): void {
    this.competences.push(this.creerCompetenceFormGroup(competence));
  }

  supprimerCompetence(index: number): void {
    this.competences.removeAt(index);
  }

  // ----------------------- Gestion Unités d'Enseignement (UE) -----------------------

  creerUniteEnseignementFormGroup(ue: Partial<UniteEnseignement> = {}): FormGroup {
    return this.fb.group({
      id: [ue.id || null],
      nom: [ue.nom || '', Validators.required],
      code: [ue.code || '', Validators.required],
      description: [ue.description || ''],
      ects: [ue.ects || 0, [Validators.required, Validators.min(0)]],
      semestre: [ue.semestre || null, Validators.required],
      volumeHoraireCours: [ue.volumeHoraireCours || 0, [Validators.required, Validators.min(0)]],
      volumeHoraireTD: [ue.volumeHoraireTD || 0, [Validators.required, Validators.min(0)]],
      volumeHoraireTP: [ue.volumeHoraireTP || 0, [Validators.required, Validators.min(0)]],
      elementConstitutifIds: [ue.elementConstitutifIds || []]
    });
  }

  ajouterUniteEnseignement(ue?: Partial<UniteEnseignement>): void {
    this.unitesEnseignement.push(this.creerUniteEnseignementFormGroup(ue));
  }

  supprimerUniteEnseignement(index: number): void {
    this.unitesEnseignement.removeAt(index);
  }

  // ----------------------- Calculs Dynamiques -----------------------

  get volumeHoraireTotal(): number {
    return this.unitesEnseignement.controls.reduce((total, ueControl) => {
      const ue = ueControl.value as UniteEnseignement;
      return total + (ue.volumeHoraireCours || 0) + (ue.volumeHoraireTD || 0) + (ue.volumeHoraireTP || 0);
    }, 0);
  }

  get ectsTotal(): number {
    return this.unitesEnseignement.controls.reduce((total, ueControl) => {
      const ue = ueControl.value as UniteEnseignement;
      return total + (ue.ects || 0);
    }, 0);
  }

  // ----------------------- Chargements initiaux -----------------------

  chargerFormations(): void {
    this.isLoading = true;
    this.formationService.getAllFormations().subscribe({
      next: data => { 
        this.formations = data; 
        this.isLoading = false; 
      },
      error: err => { 
        console.error('Erreur lors du chargement des formations', err); 
        this.isLoading = false; 
      }
    });
  }

  chargerElementsConstitutifs(): void {
    this.elementConstitutifService.findAll().subscribe({
      next: data => this.tousLesElementsConstitutifs = data,
      error: err => console.error('Erreur lors du chargement des éléments constitutifs', err)
    });
  }

  // ----------------------- Gestion années par niveau -----------------------

  updateAnneesDisponibles(niveau: NiveauEtude | undefined) {
    switch (niveau) {
      case 'LICENCE':
      case 'BACHELOR':
        this.anneesDisponibles = [
          { value: 1, label: 'LICENCE 1' },
          { value: 2, label: 'LICENCE 2' },
          { value: 3, label: 'LICENCE 3' }
        ];
        break;
      case 'MASTER':
      case 'MS':
        this.anneesDisponibles = [
          { value: 1, label: 'MASTER 1' },
          { value: 2, label: 'MASTER 2' }
        ];
        break;
      case 'CERTIFICAT':
        this.anneesDisponibles = [{ value: 1, label: 'Certificat' }];
        break;
      case 'DOCTORAT':
        this.anneesDisponibles = [
          { value: 1, label: 'Année 1' },
          { value: 2, label: 'Année 2' },
          { value: 3, label: 'Année 3' }
        ];
        break;
      default:
        this.anneesDisponibles = [{ value: 1, label: 'Niveau 1' }];
    }
    const currentAnnee = this.formationForm.get('anneeCycle')?.value;
    const isValid = this.anneesDisponibles.some(a => a.value === currentAnnee);
    if (!isValid) {
      this.formationForm.get('anneeCycle')?.setValue(this.anneesDisponibles[0].value);
    }
  }

  // ----------------------- Création / Modification -----------------------

  submitFormation(): void {
    if (this.formationForm.invalid) {
      this.formationForm.markAllAsTouched();
      console.error('Formulaire invalide', this.formationForm.errors);
      alert('Veuillez remplir tous les champs obligatoires correctement.');
      return;
    }
    
    const payload: FormationCreation = this.formationForm.value;

    if (this.editingFormationId) {
      this.formationService.modifierFormation(this.editingFormationId, payload).subscribe({
        next: () => { 
          alert('Formation mise à jour avec succès !'); 
          this.resetForm(); 
          this.chargerFormations(); 
        },
        error: err => {
          console.error(err);
          alert('Erreur lors de la mise à jour de la formation : ' + (err.error?.message || err.message));
        }
      });
    } else {
      this.formationService.creerFormation(payload).subscribe({
        next: () => { 
          alert('Formation créée avec succès !'); 
          this.resetForm(); 
          this.chargerFormations(); 
        },
        error: err => {
          console.error(err);
          alert('Erreur lors de la création de la formation : ' + (err.error?.message || err.message));
        }
      });
    }
  }

  editerFormation(f: FormationDetail): void {
    this.editingFormationId = f.id;

    // Réinitialisation des FormArray avant de patcher
    while (this.competences.length) this.competences.removeAt(0);
    while (this.unitesEnseignement.length) this.unitesEnseignement.removeAt(0);

    // Patch des valeurs principales
    const formData = {
      etablissementId: f.etablissement?.id || '',
      uefrId: f.uefr?.id || '',
      departementId: f.departement?.id || '',
      nom: f.nom,
      code: f.code,
      description: f.description || '',
      statut: f.statut,
      duree: f.duree,
      niveauEtude: f.niveauEtude,
      anneeCycle: f.anneeCycle,
      responsableId: f.responsableId,
      objectifs: f.objectifs || '',
      prerequis: f.prerequis || '',
      debouches: f.debouches || '',
      evaluationModalites: f.evaluationModalites || '',
      modaliteEnseignement: f.modaliteEnseignement,
      lieu: f.lieu || '',
      dateDebut: f.dateDebut || '',
      dateFin: f.dateFin || '',
      capacite: f.capacite,
      tarif: f.tarif,
      certificationProfessionnelle: f.certificationProfessionnelle || '',
      intervenantsIds: f.intervenantsIds || [],
      documentsIds: f.documentsIds || []
    };

    this.formationForm.patchValue(formData);

    // Charger les UEFR si un établissement est défini
    if (f.etablissement?.id) {
      this.onEtablissementChange(f.etablissement.id);
    }
    
    // Charger les départements si une UEFR est définie
    if (f.uefr?.id) {
      // Attendre que les UEFR soient chargées
      setTimeout(() => {
        this.onUefrChange(f.uefr?.id);
      }, 300);
    }

    // Patch des FormArray
    (f.competences || []).forEach(c => this.ajouterCompetence(c));
    (f.unitesEnseignement || []).forEach(ue => this.ajouterUniteEnseignement(ue));

    this.updateAnneesDisponibles(f.niveauEtude);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  resetForm(): void {
    this.formationForm.reset({
      etablissementId: '',
      uefrId: '',
      departementId: '',
      nom: '',
      code: '',
      description: '',
      statut: 'EN_PREPARATION',
      duree: 1,
      niveauEtude: 'LICENCE',
      anneeCycle: 1,
      responsableId: null,
      objectifs: '',
      prerequis: '',
      debouches: '',
      evaluationModalites: '',
      modaliteEnseignement: 'PRESENTIEL',
      lieu: '',
      dateDebut: '',
      dateFin: '',
      capacite: null,
      tarif: null,
      certificationProfessionnelle: '',
      intervenantsIds: [],
      documentsIds: []
    });
    
    // Réinitialiser les listes dépendantes
    this.uefrs = [];
    this.departements = [];
    
    // Réinitialiser les messages d'erreur
    this.errorMessageUefrs = '';
    this.errorMessageDepartements = '';
    
    // Désactiver les champs dépendants
    this.formationForm.get('uefrId')?.disable();
    this.formationForm.get('departementId')?.disable();
    
    // Réinitialiser les FormArray
    while(this.competences.length) this.competences.removeAt(0);
    while(this.unitesEnseignement.length) this.unitesEnseignement.removeAt(0);
    
    this.editingFormationId = null;
  }

  // ----------------------- Suppression -----------------------

  supprimerFormation(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette formation ? Cette action est irréversible.')) {
      this.formationService.supprimerFormation(id).subscribe({
        next: () => { 
          alert('Formation supprimée avec succès.'); 
          this.chargerFormations(); 
        },
        error: err => {
          console.error(err);
          alert('Erreur lors de la suppression de la formation : ' + (err.error?.message || err.message));
        }
      });
    }
  }

  // ----------------------- Méthode de débogage -----------------------

  testerAPI(): void {
    console.log('=== TEST DES ENDPOINTS API ===');
    
    // Test établissements
    console.log('1. Test /etablissements:');
    this.etablissementService.getAll().subscribe({
      next: data => console.log('✓ SUCCÈS -', data.length, 'établissements'),
      error: err => console.error('✗ ÉCHEC -', err)
    });
    
    // Test UEFR
    console.log('2. Test /uefrs:');
    this.uefrService.getAll().subscribe({
      next: data => console.log('✓ SUCCÈS -', data.length, 'UEFR'),
      error: err => console.error('✗ ÉCHEC -', err)
    });
    
    // Test départements
    console.log('3. Test /departements:');
    this.departementService.getAll().subscribe({
      next: data => console.log('✓ SUCCÈS -', data.length, 'départements'),
      error: err => console.error('✗ ÉCHEC -', err)
    });
  }
}