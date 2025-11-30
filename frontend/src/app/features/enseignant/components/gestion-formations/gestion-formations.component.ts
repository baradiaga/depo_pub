import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import { FormationService } from '../../../../services/formation.service';
import { ElementConstitutifService } from '../../../../services/element-constitutif.service';
import { FormationCreation, FormationDetail, ElementConstitutifResponse, RessourcePedagogique } from '../../../../models/models';

type StatutFormation = 'ACTIF' | 'ARCHIVE' | 'EN_PREPARATION';
type NiveauEtude = 'LICENCE' | 'MASTER' | 'CERTIFICAT' | 'DOCTORAT';

@Component({
  selector: 'app-gestion-formations',
  templateUrl: './gestion-formations.component.html',
  styleUrls: ['./gestion-formations.component.css']
})
export class GestionFormationsComponent implements OnInit {

  formations: FormationDetail[] = [];
  isLoading = false;

  // Données pour les sélecteurs
  tousLesElementsConstitutifs: ElementConstitutifResponse[] = [];
  statuts: StatutFormation[] = ['ACTIF', 'ARCHIVE', 'EN_PREPARATION'];
  niveaux: NiveauEtude[] = ['LICENCE', 'MASTER', 'CERTIFICAT', 'DOCTORAT'];

  // Formulaire réactif
  formationForm: FormGroup;
  editingFormationId: number | null = null;

  // Fichiers upload temporaire
  fichiersAUploader: File[] = [];

  // Intervenants pour select (facultatif)
  intervenants: any[] = [];

  constructor(
    private fb: FormBuilder,
    private formationService: FormationService,
    private elementConstitutifService: ElementConstitutifService
  ) {
    // Initialise le formulaire avec tous les champs nécessaires
    this.formationForm = this.fb.group({
      nom: ['', [Validators.required, Validators.maxLength(200)]],
      code: ['', [Validators.required, Validators.maxLength(50)]],
      description: [''],
      statut: ['EN_PREPARATION', Validators.required],
      duree: [1, [Validators.required, Validators.min(1)]],
      niveauEtude: ['LICENCE', Validators.required],
      responsableId: [null],
      // Pédagogie
      objectifs: [''],
      competences: this.fb.array([]), // tableau de string
      prerequis: [''],
      debouches: [''],
      evaluationModalites: [''],
      volumeHoraireTotal: [null],
      // Organisation
      modaliteEnseignement: ['PRESENTIEL'],
      lieu: [''],
      // Administration
      dateDebut: [''],
      dateFin: [''],
      capacite: [null],
      tarif: [null],
      // Structure
      elementsConstitutifsIds: [[]], // stocke ids []
      // Ressources / documents
      documentsIds: [[]],
      // intervenants
      intervenantsIds: [[]]
    });
  }

  ngOnInit(): void {
    this.chargerFormations();
    this.chargerElementsConstitutifs();
    this.chargerIntervenants();
  }

  // -----------------------
  // Chargements initiaux
  // -----------------------
  chargerFormations(): void {
    this.isLoading = true;
    this.formationService.getAllFormations().subscribe({
      next: data => {
        this.formations = data;
        this.isLoading = false;
      },
      error: err => {
        console.error("Erreur chargement des formations", err);
        this.isLoading = false;
      }
    });
  }

  chargerElementsConstitutifs(): void {
    this.elementConstitutifService.findAll().subscribe({
      next: data => this.tousLesElementsConstitutifs = data,
      error: err => console.error("Erreur chargement EC", err)
    });
  }

  chargerIntervenants(): void {
    this.formationService.getIntervenants().subscribe({
      next: data => this.intervenants = data,
      error: err => console.warn("Impossible de charger intervenants", err)
    });
  }

  // -----------------------
  // Helpers pour FormArray de competences
  // -----------------------
  get competences(): FormArray {
    return this.formationForm.get('competences') as FormArray;
  }

  ajouterCompetence(valeur: string = ''): void {
    this.competences.push(this.fb.control(valeur));
  }

  supprimerCompetence(index: number): void {
    this.competences.removeAt(index);
  }

  // -----------------------
  // Gestion des éléments constitutifs (checkboxes)
  // -----------------------
  toggleElementConstitutif(elementId: number): void {
    const arr: number[] = this.formationForm.get('elementsConstitutifsIds')!.value || [];
    const index = arr.indexOf(elementId);
    if (index > -1) {
      arr.splice(index, 1);
    } else {
      arr.push(elementId);
    }
    this.formationForm.get('elementsConstitutifsIds')!.setValue(arr);
  }

  isElementSelected(elementId: number): boolean {
    const arr: number[] = this.formationForm.get('elementsConstitutifsIds')!.value || [];
    return arr.indexOf(elementId) > -1;
  }

  // -----------------------
  // Création / Modification
  // -----------------------
  submitFormation(): void {
    if (this.formationForm.invalid) {
      this.formationForm.markAllAsTouched();
      alert('Veuillez corriger les champs obligatoires.');
      return;
    }

    const payload: FormationCreation = this.mapFormToPayload();

    if (this.editingFormationId) {
      // Modification
      this.formationService.modifierFormation(this.editingFormationId, payload).subscribe({
        next: updated => {
          alert('Formation mise à jour avec succès.');
          this.resetForm();
          this.chargerFormations();
          this.editingFormationId = null;
        },
        error: err => {
          console.error('Erreur mise à jour', err);
          alert('Erreur lors de la mise à jour de la formation.');
        }
      });
    } else {
      // Création
      this.formationService.creerFormation(payload).subscribe({
        next: created => {
          alert('Formation créée avec succès !');
          // si fichiers à uploader, les envoyer après création
          if (this.fichiersAUploader.length > 0) {
            this.handleUploadDocuments(created.id);
          }
          this.resetForm();
          this.chargerFormations();
        },
        error: err => {
          console.error('Erreur de création', err);
          alert('Erreur lors de la création de la formation.');
        }
      });
    }
  }

  private mapFormToPayload(): FormationCreation {
    const val = this.formationForm.value;

    // Normaliser competences (FormArray -> string[])
    const competences: string[] = (val.competences || []).filter((c: any) => !!c);

    const payload: FormationCreation = {
      nom: val.nom,
      code: val.code,
      description: val.description,
      statut: val.statut,
      duree: val.duree,
      niveauEtude: val.niveauEtude,
      responsableId: val.responsableId,
      objectifs: val.objectifs,
      competences: competences,
      prerequis: val.prerequis,
      debouches: val.debouches,
      evaluationModalites: val.evaluationModalites,
      volumeHoraireTotal: val.volumeHoraireTotal,
      modaliteEnseignement: val.modaliteEnseignement,
      lieu: val.lieu,
      dateDebut: val.dateDebut,
      dateFin: val.dateFin,
      capacite: val.capacite,
      tarif: val.tarif,
      elementsConstitutifsIds: val.elementsConstitutifsIds || [],
      intervenantsIds: val.intervenantsIds || [],
      documentsIds: val.documentsIds || []
    };

    return payload;
  }

  // Préparer l'édition d'une formation
  editerFormation(f: FormationDetail): void {
    this.editingFormationId = f.id;
    // Remplir le formulaire
    this.formationForm.patchValue({
      nom: f.nom,
      code: (f as any).code || '',
      description: f.description || '',
      statut: f.statut || 'EN_PREPARATION',
      duree: f.duree || 1,
      niveauEtude: f.niveauEtude || 'LICENCE',
      responsableId: (f as any).responsableNom ? null : null, // si tu as id responsable, set it
      objectifs: f.objectifs || '',
      competences: f.competences || [],
      prerequis: f.prerequis || '',
      debouches: f.debouches || '',
      evaluationModalites: f.evaluationModalites || '',
      volumeHoraireTotal: f.volumeHoraireTotal || null,
      modaliteEnseignement: f.modaliteEnseignement || 'PRESENTIEL',
      lieu: f.lieu || '',
      dateDebut: f.dateDebut || '',
      dateFin: f.dateFin || '',
      capacite: f.capacite || null,
      tarif: f.tarif || null,
      elementsConstitutifsIds: (f.elementsConstitutifs || []).map(ec => ec.id),
      intervenantsIds: (f.intervenants || []).map(i => i.id),
      documentsIds: (f.documents || []).map(d => d.id)
    });

    // Reset competences FormArray and fill it
    while (this.competences.length) this.competences.removeAt(0);
    (f.competences || []).forEach(c => this.ajouterCompetence(c));

    // Scroll to form or open modal in UI (selon UX)
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  // Reset form to initial state
  resetForm(): void {
    this.formationForm.reset({
      nom: '',
      code: '',
      description: '',
      statut: 'EN_PREPARATION',
      duree: 1,
      niveauEtude: 'LICENCE',
      responsableId: null,
      objectifs: '',
      competences: [],
      prerequis: '',
      debouches: '',
      evaluationModalites: '',
      volumeHoraireTotal: null,
      modaliteEnseignement: 'PRESENTIEL',
      lieu: '',
      dateDebut: '',
      dateFin: '',
      capacite: null,
      tarif: null,
      elementsConstitutifsIds: [],
      documentsIds: [],
      intervenantsIds: []
    });

    while (this.competences.length) this.competences.removeAt(0);
    this.fichiersAUploader = [];
    this.editingFormationId = null;
  }

  // -----------------------
  // Suppression
  // -----------------------
  supprimerFormation(id: number, nom: string): void {
    if (confirm(`Êtes-vous sûr de vouloir supprimer la formation : "${nom}" ?`)) {
      this.formationService.supprimerFormation(id).subscribe({
        next: () => {
          alert("Formation supprimée.");
          this.chargerFormations();
        },
        error: err => {
          console.error("Erreur de suppression", err);
          alert("Erreur lors de la suppression.");
        }
      });
    }
  }

  // -----------------------
  // Upload documents
  // -----------------------
  onFileSelected(event: any): void {
    const files: FileList = event.target.files;
    for (let i = 0; i < files.length; i++) {
      this.fichiersAUploader.push(files.item(i)!);
    }
  }

  // Après création (ou édition), uploader les documents attachés
  private handleUploadDocuments(formationId: number): void {
    const toUpload = [...this.fichiersAUploader];
    toUpload.forEach(file => {
      this.formationService.uploadDocument(formationId, file).subscribe({
        next: (res: RessourcePedagogique) => {
          console.log('Document uploadé', res);
        },
        error: err => {
          console.error('Erreur upload document', err);
        }
      });
    });
    // vider la liste locale
    this.fichiersAUploader = [];
  }

  // -----------------------
  // Téléchargement / affichage documents (exemple)
  // -----------------------
  showDocuments(formation: FormationDetail): void {
    this.formationService.getDocuments(formation.id).subscribe({
      next: docs => {
        // Logic simple : ouvrir nouveaux onglets, ou afficher modal
        console.log('Documents:', docs);
        // Ex: afficher en modal (non implémenté ici)
      },
      error: err => console.error('Impossible de charger documents', err)
    });
  }
}
