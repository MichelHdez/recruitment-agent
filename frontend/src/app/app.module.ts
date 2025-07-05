import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { CreateVacancyComponent } from './create-vacancy/create-vacancy.component';
import { VacanciesComponent } from './vacancies/vacancies.component';
import { UploadCvComponent } from './upload-cv/upload-cv.component';
import { CvsComponent } from './cvs/cvs.component';
import { MetricsComponent } from './metrics/metrics.component';
import { AptCandidatesComponent } from './apt-candidates/apt-candidates.component';

@NgModule({
  declarations: [AppComponent, CreateVacancyComponent, VacanciesComponent, UploadCvComponent, CvsComponent, MetricsComponent, AptCandidatesComponent],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
