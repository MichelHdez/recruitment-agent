import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { saveAs } from 'file-saver';

@Component({
  selector: 'app-metrics',
  templateUrl: './metrics.component.html',
  styleUrl: './metrics.component.css'
})
export class MetricsComponent {
  metrics: any = {};

  constructor(private http: HttpClient) {
    this.loadMetrics();
  }

  loadMetrics() {
    this.http.get<any>('http://localhost:8080/metrics').subscribe(data => this.metrics = data);
  }

  exportMetrics() {
    this.http.get('http://localhost:8080/metrics/export', { responseType: 'blob' })
      .subscribe(blob => {
        saveAs(blob, 'metrics.xlsx');
      });
  }

  educationKeys() {
    return this.metrics.educationDistribution ? Object.keys(this.metrics.educationDistribution) : [];
  }

  getEducationKeys() {
    return this.metrics.educationDistribution ? Object.keys(this.metrics.educationDistribution).filter(k => k !== 'Universitaria') : [];
  }

  getColor(key: string): string {
    // Colores fijos para los niveles educativos
    const colorMap: any = {
      'Licenciatura': '#4e79a7',
      'Ingeniería': '#f28e2b',
      'Maestría': '#76b7b2',
      'Doctorado': '#59a14f',
      'Técnico': '#e15759',
      'Otro': '#edc949',
      'Universitaria': '#b07aa1',
      'No especificada': '#bfc9d1'
    };
    return colorMap[key] || '#bfc9d1';
  }
}
