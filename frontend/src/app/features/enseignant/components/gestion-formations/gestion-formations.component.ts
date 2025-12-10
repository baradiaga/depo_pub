import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormArray, AbstractControl } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
// Importation des services (supposés mis à jour pour la nouvelle structure)
import { FormationService } from '../../../../services/formation.service';
import { ElementConstitutifService } from '../../../../services/element-constitutif.service';
// Importation des interfaces mises à jour
import {
  FormationCreation,
  FormationDetail,
  ElementConstitutifResponse,
  StatutFormation,
  NiveauEtude,
  ModaliteEnseignement,
  NiveauAcquisition,
  UniteEnseignement,
  CompetenceDetail
} from '../../../../models/models';

@Component({
  selector: 'app-gestion-formations',
  templateUrl: './gestion-formations.component.html',
  styleUrls: ['./gestion-formations.component.css']
})
export class GestionFormationsComponent implements OnInit {

  formations: FormationDetail[] = [];
  isLoading = false;

  // Constantes mises à jour
  tousLesElementsConstitutifs: ElementConstitutifResponse[] = [];
  statuts: StatutFormation[] = ['ACTIF', 'ARCHIVE', 'EN_PREPARATION', 'EN_VALIDATION'];
  niveaux: NiveauEtude[] = ['LICENCE', 'MASTER', 'CERTIFICAT', 'DOCTORAT', 'BACHELOR', 'MS'];
  modalites: ModaliteEnseignement[] = ['PRESENTIEL', 'DISTANCIEL', 'HYBRIDE'];
  niveauxAcquisition: NiveauAcquisition[] = ['INITIATION', 'MAITRISE', 'EXPERTISE'];

  anneesDisponibles: { value: number, label: string }[] = [];

  formationForm: FormGroup;
  editingFormationId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private formationService: FormationService,
    private elementConstitutifService: ElementConstitutifService,
    private route: ActivatedRoute
  ) {
    this.formationForm = this.fb.group({
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
      certificationProfessionnelle: [''], // Nouveau champ
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
  }

  // ----------------------- Getters pour FormArray -----------------------

  get competences(): FormArray {
    return this.formationForm.get('competences') as FormArray;
  }

  get unitesEnseignement(): FormArray {
    return this.formationForm.get('unitesEnseignement') as FormArray;
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
      semestre: [null, Validators.required],

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
      next: data => { this.formations = data; this.isLoading = false; },
      error: err => { console.error(err); this.isLoading = false; }
    });
  }

  chargerElementsConstitutifs(): void {
    this.elementConstitutifService.findAll().subscribe({
      next: data => this.tousLesElementsConstitutifs = data,
      error: err => console.error(err)
    });
  }

  // ----------------------- Gestion années par niveau -----------------------

  updateAnneesDisponibles(niveau: NiveauEtude | undefined) {
    // Logique mise à jour pour inclure les nouveaux niveaux
    switch (niveau) {
      case 'LICENCE':
      case 'BACHELOR':
        this.anneesDisponibles = [
          { value: 1, label: 'LICENCE 1' },
          { value: 2, label: 'LICENCE 2' },
          { value: 3, label: 'LICENCE3' }
        ];
        break;
      case 'MASTER':
      case 'MS':
        this.anneesDisponibles = [
          { value: 1, label: 'MASTER 1' },
          { value: 2, label: 'MASTER2' }
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
    // S'assurer que la valeur actuelle est toujours valide
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
      return;
    }
    const payload: FormationCreation = this.formationForm.value;

    if (this.editingFormationId) {
      this.formationService.modifierFormation(this.editingFormationId, payload).subscribe({
        next: () => { alert('Formation mise à jour'); this.resetForm(); this.chargerFormations(); },
        error: err => console.error(err)
      });
    } else {
      this.formationService.creerFormation(payload).subscribe({
        next: () => { alert('Formation créée'); this.resetForm(); this.chargerFormations(); },
        error: err => console.error(err)
      });
    }
  }

  editerFormation(f: FormationDetail): void {
    this.editingFormationId = f.id;

    // Réinitialisation des FormArray avant de patcher
    while (this.competences.length) this.competences.removeAt(0);
    while (this.unitesEnseignement.length) this.unitesEnseignement.removeAt(0);

    // Patch des valeurs principales
    this.formationForm.patchValue(f);

    // Patch des FormArray
    (f.competences || []).forEach(c => this.ajouterCompetence(c));
    (f.unitesEnseignement || []).forEach(ue => this.ajouterUniteEnseignement(ue));

    this.updateAnneesDisponibles(f.niveauEtude);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  resetForm(): void {
    this.formationForm.reset({
      nom:'', code:'', description:'', statut:'EN_PREPARATION', duree:1,
      niveauEtude:'LICENCE', anneeCycle:1, responsableId:null,
      objectifs:'', prerequis:'', debouches:'', evaluationModalites:'',
      modaliteEnseignement:'PRESENTIEL', lieu:'',
      dateDebut:'', dateFin:'', capacite:null, tarif:null,
      certificationProfessionnelle: '',
      intervenantsIds:[], documentsIds:[]
    });
    while(this.competences.length) this.competences.removeAt(0);
    while(this.unitesEnseignement.length) this.unitesEnseignement.removeAt(0);
    this.editingFormationId = null;
  }

  // ----------------------- Suppression -----------------------

  supprimerFormation(id: number): void {
    if (confirm('Voulez-vous vraiment supprimer cette formation ?')) {
      this.formationService.supprimerFormation(id).subscribe({
        next: () => { alert('Formation supprimée'); this.chargerFormations(); },
        error: err => console.error(err)
      });
    }
  }
}
