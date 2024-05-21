import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrivateImageGalleryComponent } from './private-image-gallery.component';

describe('PrivateImageGalleryComponent', () => {
  let component: PrivateImageGalleryComponent;
  let fixture: ComponentFixture<PrivateImageGalleryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PrivateImageGalleryComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PrivateImageGalleryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
