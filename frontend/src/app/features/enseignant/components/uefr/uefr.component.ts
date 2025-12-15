import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UefrService } from '../../../../services/uefr.service';
import { EtablissementService } from '../../../../services/etablissement.service';
import { Uefr } from '../../../../models/uefr.model';
import { Etablissement } from '../../../../models/etablissement.model';

@Component({
  selector: 'app-uefr',
  templateUrl: './uefr.component.html',
  styleUrls: ['./uefr.component.css']
})
export class UefrComponent implements OnInit {
  uefrForm: FormGroup;
  uefrs: Uefr[] = [];
  etablissements: Etablissement[] = [];
  editingId: number | null = null;
  
  constructor(
    private fb: FormBuilder,
    private uefrService: UefrService,
    private etablissementService: EtablissementService
  ) {
    this.uefrForm = this.fb.group({
      id: [null],
      nom: ['', [Validators.required, Validators.minLength(2)]],
      sigle: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(10)]],
      adresse: ['', Validators.required],
      contact: ['', Validators.required],
      logo: [''],
      lien: [''],
      etablissement_id: [null, Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadUefrs();
    this.loadEtablissements();
  }

  loadUefrs(): void {
    this.uefrService.getAll().subscribe(data => {
      this.uefrs = data;
    });
  }

  loadEtablissements(): void {
    this.etablissementService.getAll().subscribe(data => {
      this.etablissements = data;
    });
  }

  onSubmit(): void {
    if (this.uefrForm.valid) {
      const uefr = this.uefrForm.value;
      
      if (this.editingId) {
        this.uefrService.update(this.editingId, uefr).subscribe(() => {
          this.resetForm();
          this.loadUefrs();
        });
      } else {
        this.uefrService.create(uefr).subscribe(() => {
          this.resetForm();
          this.loadUefrs();
        });
      }
    }
  }

  editUefr(uefr: Uefr): void {
  this.editingId = uefr.id ?? null;  // <-- ici
  this.uefrForm.patchValue(uefr);
}
getEtablissementName(id?: number): string {
  if (!id) return '';
  const etab = this.etablissements.find(e => e.id === id);
  return etab ? etab.nom : '';
}

deleteUefr(uefr: Uefr): void {
  if (!uefr.id) return; // sécurité si id est undefined
  this.uefrService.delete(uefr.id).subscribe(() => {
    // rafraîchir la liste après suppression
    this.loadUefrs();
  });
}


  resetForm(): void {
    this.uefrForm.reset();
    this.editingId = null;
  }

  cancelEdit(): void {
    this.resetForm();
  }
}
