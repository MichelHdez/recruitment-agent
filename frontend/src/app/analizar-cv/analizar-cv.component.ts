import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-analizar-cv',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './analizar-cv.component.html',
  styleUrl: './analizar-cv.component.css'
})
export class AnalizarCvComponent implements OnInit {
  vacantes: any[] = [];
  vacanteId: number | null = null;
  cvText: string = '';
  feedback: any = null;
  loading = false;
  guardando = false;
  guardado = false;
  errorGuardado = false;
  candidateName: string = '';
  education: string = '';

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.http.get<any[]>('http://localhost:8080/vacancies').subscribe(data => this.vacantes = data);
  }

  analizar() {
    if (!this.vacanteId || !this.cvText || !this.candidateName.trim()) return;
    this.loading = true;
    this.feedback = null;
    this.guardado = false;
    this.errorGuardado = false;
    const vacante = this.vacantes.find(v => v.id === this.vacanteId);
    if (!vacante) return;
    // Solo analizar, no guardar
    this.http.post<any>('http://localhost:8080/cvs/analizar', {
      cvText: this.cvText,
      vacancyId: this.vacanteId
    }).subscribe({
      next: (analysis) => {
        // Mostrar el score como porcentaje entero
        let score = analysis.cohereScore;
        if (typeof score === 'number') {
          score = Math.round(score * 100);
        } else {
          score = null;
        }
        this.feedback = {
          cohereScore: score,
          cohereSummary: analysis.cohereSummary,
          status: analysis.status
        };
        this.loading = false;
      },
      error: () => {
        this.feedback = { cohereScore: null, cohereSummary: 'No se pudo obtener el análisis.' };
        this.loading = false;
      }
    });
  }

  guardarCV() {
    if (!this.vacanteId || !this.cvText || !this.candidateName.trim() || !this.education.trim()) return;
    this.guardando = true;
    this.guardado = false;
    this.errorGuardado = false;
    this.http.post<any>('http://localhost:8080/cvs', {
      candidateName: this.candidateName,
      cvText: this.cvText,
      vacancyId: this.vacanteId,
      education: this.education
    }).subscribe({
      next: () => {
        this.guardando = false;
        this.guardado = true;
      },
      error: () => {
        this.guardando = false;
        this.errorGuardado = true;
      }
    });
  }
}
