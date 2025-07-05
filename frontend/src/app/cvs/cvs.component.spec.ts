import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CvsComponent } from './cvs.component';

describe('CvsComponent', () => {
  let component: CvsComponent;
  let fixture: ComponentFixture<CvsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CvsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CvsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
