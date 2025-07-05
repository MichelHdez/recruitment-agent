import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { saveAs } from 'file-saver';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent {
  vacancies: any[] = [];
  metrics: any = {};
  selectedVacancy: any = null;
  candidateName: string = '';
  file: File | null = null;
  newVacancy = { title: '', description: '', requirements: '', location: '', experienceYears: null };
  cvs: any[] = [];
  // Filtros avanzados
  filter = { skills: '', experienceYears: null, location: '' };

  constructor(private readonly http: HttpClient) {
    this.loadVacancies();
    this.loadMetrics();
    this.loadCVs();
  }

  loadVacancies() {
    this.http.get<any[]>('http://localhost:8080/vacancies').subscribe(data => this.vacancies = data);
  }

  loadMetrics() {
    this.http.get<any>('http://localhost:8080/metrics').subscribe(data => this.metrics = data);
  }

  loadCVs() {
    this.http.get<any[]>('http://localhost:8080/cvs').subscribe(data => {
      this.cvs = data;
    });
  }

  createVacancy() {
    this.http.post('http://localhost:8080/vacancies', this.newVacancy)
      .subscribe(() => {
        this.loadVacancies();
        this.newVacancy = { title: '', description: '', requirements: '', location: '', experienceYears: null };
      });
  }

  onFileChange(event: any) {
    this.file = event.target.files[0];
  }

  uploadCV() {
    if (!this.file || !this.selectedVacancy) return;
    const formData = new FormData();
    formData.append('file', this.file);
    formData.append('candidateName', this.candidateName);
    formData.append('vacancyId', this.selectedVacancy.id);

    this.http.post('/api/cvs/upload', formData).subscribe({
      next: () => {
        this.loadCVs(); // <-- Esto refresca la lista
        this.file = null;
        this.candidateName = '';
      },
      error: err => alert('Error al subir el CV')
    });
  }

  downloadCV(cv: any) {
    this.http.get(`http://localhost:8080/cvs/${cv.id}/file`, { responseType: 'blob' })
      .subscribe(blob => {
        saveAs(blob, cv.fileName);
      });
  }

  searchCVs() {
    const params: any = {};
    if (this.filter.skills) params.skills = this.filter.skills;
    if (this.filter.experienceYears) params.experienceYears = this.filter.experienceYears;
    if (this.filter.location) params.location = this.filter.location;
    this.http.get<any[]>('http://localhost:8080/cvs/search', { params })
      .subscribe(data => this.cvs = data);
  }

  searchVacancies() {
    const params: any = {};
    if (this.filter.location) params.location = this.filter.location;
    if (this.filter.experienceYears) params.experienceYears = this.filter.experienceYears;
    if (this.filter.skills) params.requirements = this.filter.skills;
    this.http.get<any[]>('http://localhost:8080/vacancies/search', { params })
      .subscribe(data => this.vacancies = data);
  }
}
