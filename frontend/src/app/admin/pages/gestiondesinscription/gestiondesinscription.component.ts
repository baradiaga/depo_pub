import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, FormControl, Validators } from '@angular/forms';
import { EtudiantService, EtudiantDto } from '../../../services/etudiant.service'; // Adaptez le chemin
// Assurez-vous que NouvelleMatiereDto est bien exportée depuis votre service
import { MatiereService, MatiereAvecDetails, NouvelleMatiereDto } from '../../../services/matiere.service'; // Adaptez le chemin

@Component({
  selector: 'app-gestiondesinscription',
  templateUrl: './gestiondesinscription.component.html',
  styleUrls: ['./gestiondesinscription.component.css']
})
export class GestionDesInscriptionComponent implements OnInit {
  
  inscriptionForm!: FormGroup;
  matieresDisponibles: MatiereAvecDetails[] = [];
  inscriptions: EtudiantDto[] = []; 
  
  // ====================================================================
  // --- DÉCLARATION DES PROPRIÉTÉS POUR L'AJOUT DE MATIÈRE ---
  // C'est cette section qui manquait et causait les erreurs.
  // ====================================================================
  addMatiereForm: FormGroup;
  showAddMatiereModal = false; // Pour contrôler l'affichage de la modale
  addMatiereError: string | null = null;
  
  constructor(
    private fb: FormBuilder,
    private etudiantService: EtudiantService,
    private matiereService: MatiereService
  ) {
    // Initialisation du formulaire d'ajout de matière
    this.addMatiereForm = this.fb.group({
      nom: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.initForm();
    this.chargerDonneesInitiales();
  }

  initForm(): void {
    this.inscriptionForm = this.fb.group({
      id: [null],
      nom: ['', Validators.required],
      prenom: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      dateDeNaissance: [null, Validators.required],
      lieuDeNaissance: [''],
      nationalite: [''],
      sexe: [null, Validators.required],
      adresse: [''],
      telephone: ['', Validators.required],
      anneeAcademique: ['2024-2025', Validators.required],
      filiere: ['', Validators.required],
      enseignantId: [null],
      matiereIds: this.fb.array([], Validators.required)
    });
  }

  chargerDonneesInitiales(): void {
    this.etudiantService.getEtudiants().subscribe(data => this.inscriptions = data);
    // Assurez-vous que votre service a bien une méthode getMatieres()
    this.matiereService.getMatieres().subscribe(data => this.matieresDisponibles = data);
  }

  onMatiereChange(event: any): void {
    const matieresFormArray: FormArray = this.inscriptionForm.get('matiereIds') as FormArray;
    const matiereId = Number(event.target.value);

    if (event.target.checked) {
      matieresFormArray.push(new FormControl(matiereId));
    } else {
      const index = matieresFormArray.controls.findIndex(x => x.value === matiereId);
      matieresFormArray.removeAt(index);
    }
  }

  enregistrer(): void {
    if (this.inscriptionForm.invalid) {
      alert('Le formulaire est invalide. Veuillez vérifier tous les champs obligatoires.');
      return;
    }

    const etudiantDto: EtudiantDto = this.inscriptionForm.value;
    const operation = etudiantDto.id 
      ? this.etudiantService.updateEtudiant(etudiantDto.id, etudiantDto)
      : this.etudiantService.inscrireEtudiant(etudiantDto);

    operation.subscribe({
      next: () => {
        alert(`Étudiant ${etudiantDto.id ? 'mis à jour' : 'inscrit'} avec succès !`);
        this.resetFormulaire();
        this.chargerDonneesInitiales();
      },
      error: (err) => alert(`Erreur : ${err.error.message || err.message}`)
    });
  }

  modifier(etudiant: EtudiantDto): void {
    this.inscriptionForm.patchValue(etudiant);
    const matieresFormArray = this.inscriptionForm.get('matiereIds') as FormArray;
    matieresFormArray.clear();
    if (etudiant.matiereIds) {
      etudiant.matiereIds.forEach(id => matieresFormArray.push(new FormControl(id)));
    }
  }
  
  supprimer(id: number | undefined): void {
    if (!id) return;
    if (confirm('Voulez-vous vraiment supprimer cet étudiant ?')) {
      this.etudiantService.deleteEtudiant(id).subscribe({
        next: () => {
          alert('Étudiant supprimé avec succès.');
          this.chargerDonneesInitiales();
        },
        error: (err) => alert(`Erreur : ${err.error.message || err.message}`)
      });
    }
  }

  annuler(): void {
    this.resetFormulaire();
  }

  private resetFormulaire(): void {
    this.inscriptionForm.reset({ anneeAcademique: '2024-2025' });
    (this.inscriptionForm.get('matiereIds') as FormArray).clear();
  }

  // ====================================================================
  // --- DÉFINITION DES MÉTHODES POUR L'AJOUT DE MATIÈRE ---
  // C'est cette section qui manquait et causait les erreurs.
  // ====================================================================

  /**
   * Ouvre la fenêtre modale pour ajouter une matière.
   */
  openAddMatiereModal(): void {
    this.showAddMatiereModal = true;
    this.addMatiereError = null;
    this.addMatiereForm.reset();
  }

  /**
   * Ferme la fenêtre modale.
   */
  closeAddMatiereModal(): void {
    this.showAddMatiereModal = false;
  }

  /**
   * Gère la soumission du formulaire d'ajout de matière.
   */
  onAjouterMatiere(): void {
    if (this.addMatiereForm.invalid) {
      return;
    }

    const nouvelleMatiere: NouvelleMatiereDto = this.addMatiereForm.value;
    this.addMatiereError = null;

    this.matiereService.addMatiere(nouvelleMatiere).subscribe({
      next: (matiereCreee) => {
        this.matieresDisponibles.push(matiereCreee);
        this.closeAddMatiereModal();
      },
      error: (err) => {
        if (err.status === 409) {
          this.addMatiereError = err.error;
        } else {
          this.addMatiereError = "Une erreur technique est survenue lors de l'ajout.";
        }
        console.error('Erreur lors de l\'ajout de la matière:', err);
      }
    });
  }
}
