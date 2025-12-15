import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DepartementService } from '../../../../services/departement.service';
import { UefrService } from '../../../../services/uefr.service';
import { Departement } from '../../../../models/departement.model';
import { Uefr } from '../../../../models/uefr.model';

@Component({
  selector: 'app-departement',
  templateUrl: './departement.component.html',
  styleUrls: ['./departement.component.css']
})
export class DepartementComponent implements OnInit {
  departementForm: FormGroup;
  departementId: number | null = null;

  departements: Departement[] = [];
  uefrs: Uefr[] = [];
  editingId: number | null = null;
  
  constructor(
    private fb: FormBuilder,
    private departementService: DepartementService,
    private uefrService: UefrService
  ) {
    this.departementForm = this.fb.group({
      id: [null],
      nom: ['', [Validators.required, Validators.minLength(2)]],
      sigle: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(10)]],
      adresse: ['', Validators.required],
      contact: ['', Validators.required],
      logo: [''],
      lien: [''],
      uefr_id: [null, Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadDepartements();
    this.loadUefrs();
  }

  loadDepartements(): void {
    this.departementService.getAll().subscribe(data => {
      this.departements = data;
    });
  }

  loadUefrs(): void {
    this.uefrService.getAll().subscribe(data => {
      this.uefrs = data;
    });
  }

  onSubmit(): void {
    if (this.departementForm.valid) {
      const departement = this.departementForm.value;
      
      if (this.editingId) {
        this.departementService.update(this.editingId, departement).subscribe(() => {
          this.resetForm();
          this.loadDepartements();
        });
      } else {
        this.departementService.create(departement).subscribe(() => {
          this.resetForm();
          this.loadDepartements();
        });
      }
    }
  }

 editDepartement(departement: Departement): void {
  this.departementId = departement.id ?? null;
  this.departementForm.patchValue(departement);
}



  deleteDepartement(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce département ?')) {
      this.departementService.delete(id).subscribe(() => {
        this.loadDepartements();
      });
    }
  }

  resetForm(): void {
    this.departementForm.reset();
    this.editingId = null;
  }

  cancelEdit(): void {
    this.resetForm();
  }

  getUefrName(uefrId: number): string {
    const uefr = this.uefrs.find(u => u.id === uefrId);
    return uefr ? uefr.nom : 'Inconnu';
  }
}
