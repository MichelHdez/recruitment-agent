import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CreateVacancyComponent } from './create-vacancy/create-vacancy.component';
import { VacanciesComponent } from './vacancies/vacancies.component';
import { UploadCvComponent } from './upload-cv/upload-cv.component';
import { CvsComponent } from './cvs/cvs.component';
import { MetricsComponent } from './metrics/metrics.component';
import { AptCandidatesComponent } from './apt-candidates/apt-candidates.component';
import { AnalizarCvComponent } from './analizar-cv/analizar-cv.component';

const routes: Routes = [
  { path: '', redirectTo: 'vacancies', pathMatch: 'full' },
  { path: 'create-vacancy', component: CreateVacancyComponent },
  { path: 'vacancies', component: VacanciesComponent },
  { path: 'upload-cv', component: UploadCvComponent },
  { path: 'cvs', component: CvsComponent },
  { path: 'metrics', component: MetricsComponent },
  { path: 'apt-candidates', component: AptCandidatesComponent },
  { path: 'analizar-cv', component: AnalizarCvComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
