import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-upload-cv',
  templateUrl: './upload-cv.component.html',
  styleUrls: ['./upload-cv.component.css']
})
export class UploadCvComponent {
  candidateName: string = '';
  selectedVacancy: any = null;
  vacancies: any[] = [];

  // Para análisis de compatibilidad
  cvText: string = '';
  analysisResult: string = '';
  analysisLoading: boolean = false;
  analysisError: string = '';
  candidateNameError: string = '';

  candidateExperience: number | null = null;
  candidateExperienceError: string = '';
  saveSuccess: string = '';
  saveError: string = '';

  constructor(private http: HttpClient) {
    this.loadVacancies();
  }

  loadVacancies() {
    this.http.get<any[]>('http://localhost:8080/vacancies').subscribe(data => this.vacancies = data);
  }

  onVacancyKeyDown(event: KeyboardEvent, vacancy: any) {
    if (event.key === 'Enter' || event.key === ' ') {
      this.selectedVacancy = vacancy;
    }
  }

  async analyzeCVText() {
    this.candidateNameError = '';
    if (!this.candidateName || this.candidateName.trim() === '') {
      this.candidateNameError = 'El nombre del candidato es obligatorio para el análisis.';
      return;
    }
    if (!this.cvText || !this.selectedVacancy) return;
    this.analysisResult = '';
    this.analysisError = '';
    this.analysisLoading = true;
    try {
      const response: any = await this.http.post('http://localhost:8080/cvs/analizar', {
        cvText: this.cvText,
        vacancyId: this.selectedVacancy.id
      }).toPromise();
      if (response && (response.cohereSummary || response.status)) {
        // Mostrar el resumen de Cohere o el status
        this.analysisResult = (response.cohereSummary || response.status || '').replace(/\n/g, '<br>');
      } else {
        this.analysisError = 'No se pudo obtener un análisis válido.';
      }
    } catch (err) {
      this.analysisError = 'Error al analizar compatibilidad. Intenta de nuevo.';
    }
    this.analysisLoading = false;
  }

  async saveCandidateAnalysis() {
    this.saveSuccess = '';
    this.saveError = '';
    this.candidateNameError = '';
    this.candidateExperienceError = '';
    if (!this.candidateName || this.candidateName.trim() === '') {
      this.candidateNameError = 'El nombre del candidato es obligatorio.';
      return;
    }
    if (this.candidateExperience == null || this.candidateExperience < 0) {
      this.candidateExperienceError = 'Los años de experiencia deben ser 0 o más.';
      return;
    }
    if (!this.analysisResult) {
      this.saveError = 'Primero realiza el análisis de compatibilidad.';
      return;
    }
    try {
      const payload = {
        candidateName: this.candidateName,
        cvText: this.cvText,
        vacancyId: this.selectedVacancy.id,
        experienceYears: this.candidateExperience
      };
      await this.http.post('http://localhost:8080/cvs', payload).toPromise();
      this.saveSuccess = 'Candidato registrado correctamente.';
    } catch (err) {
      this.saveError = 'Error al registrar el candidato. Intenta de nuevo.';
    }
  }
}
