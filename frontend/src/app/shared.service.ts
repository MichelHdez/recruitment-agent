import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class SharedService {
  private readonly vacanciesSource = new BehaviorSubject<any[]>([]);
  vacancies$ = this.vacanciesSource.asObservable();

  private readonly cvsSource = new BehaviorSubject<any[]>([]);
  cvs$ = this.cvsSource.asObservable();

  updateVacancies(vacancies: any[]) {
    this.vacanciesSource.next(vacancies);
  }

  updateCVs(cvs: any[]) {
    this.cvsSource.next(cvs);
  }
}
