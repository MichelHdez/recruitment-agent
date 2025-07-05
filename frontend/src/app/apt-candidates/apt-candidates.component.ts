import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-apt-candidates',
  templateUrl: './apt-candidates.component.html',
  styleUrls: ['./apt-candidates.component.css']
})
export class AptCandidatesComponent implements OnInit {
  vacancies: any[] = [];
  selectedVacancy: any = null;
  aptCVs: any[] = [];
  loading: boolean = false;

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.loadVacancies();
  }

  loadVacancies() {
    this.http.get<any[]>('http://localhost:8080/vacancies').subscribe(data => this.vacancies = data);
  }

  onVacancyChange() {
    if (!this.selectedVacancy) {
      this.aptCVs = [];
      return;
    }
    this.loading = true;
    this.http.get<any[]>(`http://localhost:8080/cvs/apt-for-vacancy/${this.selectedVacancy.id}`)
      .subscribe(data => {
        this.aptCVs = data;
        this.loading = false;
      }, () => this.loading = false);
  }
}
