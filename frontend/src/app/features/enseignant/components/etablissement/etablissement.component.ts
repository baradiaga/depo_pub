import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { EtablissementService } from '../../../../services/etablissement.service';
import { Etablissement } from '../../../../models/etablissement.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-etablissement',
  templateUrl: './etablissement.component.html',
  styleUrls: ['./etablissement.component.css']
})
export class EtablissementComponent implements OnInit {
  etablissementForm: FormGroup;
  etablissements: Etablissement[] = [];
  editingId: number | null = null;
  
  constructor(
    private fb: FormBuilder,
    private etablissementService: EtablissementService,
    private router: Router
  ) {
    this.etablissementForm = this.fb.group({
      id: [null],
      nom: ['', [Validators.required, Validators.minLength(2)]],
      sigle: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(10)]],
      adresse: ['', Validators.required],
      contact: ['', Validators.required],
      logo: [''],
      lien: ['']
    });
  }

  ngOnInit(): void {
    this.loadEtablissements();
  }

  loadEtablissements(): void {
    this.etablissementService.getAll().subscribe(data => {
      this.etablissements = data;
    });
  }

  onSubmit(): void {
    if (this.etablissementForm.valid) {
      const etablissement = this.etablissementForm.value;
      
      if (this.editingId) {
        this.etablissementService.update(this.editingId, etablissement).subscribe(() => {
          this.resetForm();
          this.loadEtablissements();
        });
      } else {
        this.etablissementService.create(etablissement).subscribe(() => {
          this.resetForm();
          this.loadEtablissements();
        });
      }
    }
  }

  editEtablissement(etablissement: Etablissement): void {
  // Assurez-vous que editingId accepte null si l'id est optionnel
  this.editingId = etablissement.id ?? null;
  this.etablissementForm.patchValue(etablissement);
}


  deleteEtablissement(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer cet établissement ?')) {
      this.etablissementService.delete(id).subscribe(() => {
        this.loadEtablissements();
      });
    }
  }

  resetForm(): void {
    this.etablissementForm.reset();
    this.editingId = null;
  }

  cancelEdit(): void {
    this.resetForm();
  }
}
