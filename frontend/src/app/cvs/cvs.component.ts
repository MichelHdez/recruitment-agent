import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { SharedService } from '../shared.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-cvs',
  templateUrl: './cvs.component.html',
  styleUrls: ['./cvs.component.css']
})
export class CvsComponent {
  cvs: any[] = [];
  page = 0;
  size = 10;
  totalPages = 1;
  pageSize = 6;

  constructor(private http: HttpClient, private shared: SharedService) {
    this.loadCVs();
    this.shared.cvs$.subscribe(data => {
      if (data.length) this.cvs = data;
    });
  }

  loadCVs(page: number = 0) {
    this.http.get<any>(`http://localhost:8080/cvs/paged?page=${page}&size=${this.size}`).subscribe(data => {
      this.cvs = data.content;
      this.page = data.number;
      this.totalPages = data.totalPages;
    });
  }

  nextPage() {
    if (this.page < this.totalPages - 1) {
      this.loadCVs(this.page + 1);
    }
  }

  prevPage() {
    if (this.page > 0) {
      this.loadCVs(this.page - 1);
    }
  }

  hasApto(): boolean {
    return this.cvs.some(cv => cv.status === 'Apto');
  }

  async editCV(cv: any) {
    const { value: formValues } = await Swal.fire({
      title: 'Editar CV',
      html:
        `<input id="swal-input1" class="swal2-input" placeholder="Nombre" value="${cv.candidateName}">` +
        `<input id="swal-input2" class="swal2-input" placeholder="Estado (Apto, No Apto, Revisión Manual)" value="${cv.status}">`,
      focusConfirm: false,
      preConfirm: () => {
        return [
          (document.getElementById('swal-input1') as HTMLInputElement).value,
          (document.getElementById('swal-input2') as HTMLInputElement).value
        ];
      }
    });
    if (formValues && formValues[0] && formValues[1]) {
      const updatedCV = { ...cv, candidateName: formValues[0], status: formValues[1] };
      this.http.put(`http://localhost:8080/cvs/${cv.id}`, updatedCV).subscribe({
        next: () => {
          this.loadCVs();
          Swal.fire({ icon: 'success', title: 'CV editado correctamente', showConfirmButton: false, timer: 1500 });
        },
        error: () => {
          Swal.fire({ icon: 'error', title: 'Error al editar el CV', text: 'Intenta de nuevo.' });
        }
      });
    }
  }

  deleteCV(cv: any) {
    Swal.fire({
      title: '¿Eliminar CV?',
      text: 'Esta acción no se puede deshacer',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar',
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6'
    }).then(result => {
      if (result.isConfirmed) {
        this.http.delete(`http://localhost:8080/cvs/${cv.id}`).subscribe({
          next: () => {
            this.loadCVs();
            Swal.fire({ icon: 'success', title: 'CV eliminado', showConfirmButton: false, timer: 1500 });
          },
          error: () => {
            Swal.fire({ icon: 'error', title: 'Error al eliminar', text: 'No se pudo eliminar el CV.' });
          }
        });
      }
    });
  }
}
