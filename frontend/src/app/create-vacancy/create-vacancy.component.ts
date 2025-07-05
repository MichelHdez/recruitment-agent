import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { SharedService } from '../shared.service';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-create-vacancy',
  templateUrl: './create-vacancy.component.html',
  styleUrl: './create-vacancy.component.css'
})
export class CreateVacancyComponent {
  newVacancy = { title: '', description: '', requirements: '', location: '', experienceYears: null };
  descriptionCharCount: number = 0;
  requirementsCharCount: number = 0;

  constructor(private http: HttpClient, private shared: SharedService, private router: Router) {}

  updateCharCount(field: string) {
    if (field === 'description') {
      this.descriptionCharCount = this.newVacancy.description.length;
    } else if (field === 'requirements') {
      this.requirementsCharCount = this.newVacancy.requirements.length;
    }
  }

  createVacancy() {
    if (!this.newVacancy.title.trim()) {
      Swal.fire({ icon: 'warning', title: 'El título es obligatorio.' });
      return;
    }
    if (!this.newVacancy.description.trim()) {
      Swal.fire({ icon: 'warning', title: 'La descripción es obligatoria.' });
      return;
    }
    if (this.newVacancy.description.length > 255) {
      Swal.fire({ icon: 'warning', title: 'La descripción no puede superar los 255 caracteres.' });
      return;
    }
    if (!this.newVacancy.requirements.trim()) {
      Swal.fire({ icon: 'warning', title: 'Los requisitos son obligatorios.' });
      return;
    }
    if (this.newVacancy.requirements.length > 255) {
      Swal.fire({ icon: 'warning', title: 'Los requisitos no pueden superar los 255 caracteres.' });
      return;
    }
    if (this.newVacancy.experienceYears == null || this.newVacancy.experienceYears < 1) {
      Swal.fire({ icon: 'warning', title: 'Los años de experiencia deben ser 1 o más.' });
      return;
    }
    this.http.post('http://localhost:8080/vacancies', this.newVacancy)
      .subscribe({
        next: () => {
          this.http.get<any[]>('http://localhost:8080/vacancies').subscribe(vacancies => {
            this.shared.updateVacancies(vacancies);
          });
          this.newVacancy = { title: '', description: '', requirements: '', location: '', experienceYears: null };
          this.descriptionCharCount = 0;
          this.requirementsCharCount = 0;
          Swal.fire({ icon: 'success', title: 'Vacante creada correctamente', showConfirmButton: false, timer: 1500 });
          this.router.navigate(['/vacancies']);
        },
        error: () => {
          Swal.fire({ icon: 'error', title: 'Error al crear la vacante', text: 'Intenta de nuevo.' });
        }
      });
  }
}
