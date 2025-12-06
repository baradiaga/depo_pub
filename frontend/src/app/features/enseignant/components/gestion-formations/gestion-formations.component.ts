import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { FormationService } from '../../../../services/formation.service';
import { ElementConstitutifService } from '../../../../services/element-constitutif.service';
import { FormationCreation, FormationDetail, ElementConstitutifResponse } from '../../../../models/models';

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

  tousLesElementsConstitutifs: ElementConstitutifResponse[] = [];
  statuts: StatutFormation[] = ['ACTIF', 'ARCHIVE', 'EN_PREPARATION'];
  niveaux: NiveauEtude[] = ['LICENCE', 'MASTER', 'CERTIFICAT', 'DOCTORAT'];

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
      competences: this.fb.array([]),
      prerequis: [''],
      debouches: [''],
      evaluationModalites: [''],
      volumeHoraireTotal: [null],
      modaliteEnseignement: ['PRESENTIEL'],
      lieu: [''],
      dateDebut: [''],
      dateFin: [''],
      capacite: [null],
      tarif: [null],
      elementsConstitutifsIds: [[]],
      intervenantsIds: [[]],
      documentsIds: [[]]
    });
  }

  ngOnInit(): void {
    this.chargerFormations();
    this.chargerElementsConstitutifs();

    // Vérifier si on est en mode édition
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

  // ----------------------- FormArray Compétences -----------------------
  get competences(): FormArray {
    return this.formationForm.get('competences') as FormArray;
  }

  ajouterCompetence(valeur: string = ''): void {
    this.competences.push(this.fb.control(valeur));
  }

  supprimerCompetence(index: number): void {
    this.competences.removeAt(index);
  }

  // ----------------------- Gestion années par niveau -----------------------
  updateAnneesDisponibles(niveau: NiveauEtude | undefined) {
    switch (niveau) {
      case 'LICENCE':
        this.anneesDisponibles = [
          { value: 1, label: 'Licence 1' },
          { value: 2, label: 'Licence 2' },
          { value: 3, label: 'Licence 3' }
        ];
        break;
      case 'MASTER':
        this.anneesDisponibles = [
          { value: 1, label: 'Master 1' },
          { value: 2, label: 'Master 2' }
        ];
        break;
      case 'CERTIFICAT':
        this.anneesDisponibles = [{ value: 1, label: 'Certificat' }];
        break;
      case 'DOCTORAT':
        this.anneesDisponibles = [
          { value: 1, label: 'Doctorat 1' },
          { value: 2, label: 'Doctorat 2' },
          { value: 3, label: 'Doctorat 3' }
        ];
        break;
      default:
        this.anneesDisponibles = [{ value: 1, label: 'Niveau 1' }];
    }
    this.formationForm.get('anneeCycle')?.setValue(this.anneesDisponibles[0].value);
  }

  // ----------------------- Création / Modification -----------------------
  submitFormation(): void {
    if (this.formationForm.invalid) { this.formationForm.markAllAsTouched(); return; }
    const val = this.formationForm.value;
    const payload: FormationCreation = { ...val, annee: val.anneeCycle };

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
    this.formationForm.patchValue(f);

    while (this.competences.length) this.competences.removeAt(0);
    (f.competences || []).forEach(c => this.ajouterCompetence(c));

    this.updateAnneesDisponibles(f.niveauEtude);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  resetForm(): void {
    this.formationForm.reset({
      nom:'', code:'', description:'', statut:'EN_PREPARATION', duree:1,
      niveauEtude:'LICENCE', anneeCycle:1, responsableId:null,
      objectifs:'', prerequis:'', debouches:'', evaluationModalites:'',
      volumeHoraireTotal:null, modaliteEnseignement:'PRESENTIEL', lieu:'',
      dateDebut:'', dateFin:'', capacite:null, tarif:null,
      elementsConstitutifsIds:[], intervenantsIds:[], documentsIds:[]
    });
    while(this.competences.length) this.competences.removeAt(0);
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
