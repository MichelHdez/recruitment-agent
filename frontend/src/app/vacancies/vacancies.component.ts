import { Component, OnDestroy } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { SharedService } from '../shared.service';
import { Subject, Subscription } from 'rxjs';
import { debounceTime } from 'rxjs/operators';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-vacancies',
  templateUrl: './vacancies.component.html',
  styleUrls: ['./vacancies.component.css']
})
export class VacanciesComponent implements OnDestroy {
  vacancies: any[] = [];
  selectedVacancy: any = null;
  editingVacancy: any = null;

  currentPage: number = 0;
  pageSize: number = 2; // Cambiado a 2 elementos por página
  totalPages: number = 0;
  filter = { skills: '', experienceYears: null, location: '' };

  private filterSubject = new Subject<void>();
  private filterSub: Subscription;

  editSuccess: string = '';
  deleteConfirmId: number | null = null;

  constructor(private readonly http: HttpClient, private readonly shared: SharedService) {
    this.loadVacancies();
    this.shared.vacancies$.subscribe(data => {
      if (data.length) this.vacancies = data;
    });
    this.filterSub = this.filterSubject.pipe(debounceTime(400)).subscribe(() => {
      this.currentPage = 0;
      this.loadVacancies();
    });
  }

  ngOnDestroy() {
    this.filterSub.unsubscribe();
  }

  onFilterChange() {
    this.filterSubject.next();
  }

  loadVacancies() {
    const params: any = { page: this.currentPage, size: this.pageSize };
    if (this.filter.location) params.location = this.filter.location;
    if (this.filter.experienceYears && this.filter.experienceYears >= 1) params.experienceYears = this.filter.experienceYears;
    if (this.filter.skills) params.requirements = this.filter.skills;

    this.http.get<any>(`http://localhost:8080/vacancies/search`, { params })
      .subscribe(response => {
        this.vacancies = response.content;
        this.totalPages = response.totalPages;
        this.currentPage = response.number;
      });
  }

  nextPage() {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadVacancies();
    }
  }

  prevPage() {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadVacancies();
    }
  }

  editVacancy(vacancy: any) {
    this.editingVacancy = { ...vacancy };
  }

  cancelEdit() {
    this.editingVacancy = null;
  }

  saveVacancy() {
    if (!this.editingVacancy) return;
    this.http.put(`http://localhost:8080/vacancies/${this.editingVacancy.id}`, this.editingVacancy)
      .subscribe({
        next: () => {
          this.loadVacancies();
          this.editingVacancy = null;
          Swal.fire({
            icon: 'success',
            title: 'Vacante editada correctamente',
            showConfirmButton: false,
            timer: 1800
          });
        },
        error: () => {
          Swal.fire({
            icon: 'error',
            title: 'Error al editar la vacante',
            text: 'Intenta de nuevo.'
          });
        }
      });
  }

  confirmDeleteVacancy(id: number) {
    Swal.fire({
      title: '¿Eliminar vacante?',
      text: 'Esta acción no se puede deshacer',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar',
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6'
    }).then(result => {
      if (result.isConfirmed) {
        this.deleteVacancy(id);
      }
    });
  }

  deleteVacancy(id: number) {
    this.http.delete(`http://localhost:8080/vacancies/${id}`)
      .subscribe({
        next: () => {
          this.loadVacancies();
          Swal.fire({
            icon: 'success',
            title: 'Vacante eliminada',
            showConfirmButton: false,
            timer: 1500
          });
        },
        error: () => {
          Swal.fire({
            icon: 'error',
            title: 'Error al eliminar',
            text: 'No se pudo eliminar la vacante.'
          });
        }
      });
  }
}
