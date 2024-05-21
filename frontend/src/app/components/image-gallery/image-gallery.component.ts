import {HttpEventType, HttpParams} from '@angular/common/http';
import { Component } from '@angular/core';
import {NgxDropzoneChangeEvent} from "ngx-dropzone";
import {ImageService} from "../../service/image-service";
import {ImageMetadata} from "../../domain/ImageMetadata";
import {MatSnackBar} from "@angular/material/snack-bar";
import {GalleryType} from "../../domain/enums/GalleryType";

@Component({
  selector: 'app-image-gallery',
  templateUrl: './image-gallery.component.html',
  styleUrl: './image-gallery.component.scss'
})
export class ImageGalleryComponent {
  constructor(
    private imageService: ImageService,
    private _snackBar: MatSnackBar
  ) {};

  allowedExtensions: string[] = ['.jpg', '.jpeg', '.png'];
  files: File[] = [];
  images: ImageMetadata[] = [];
  searchKeyword: string = '';
  pageNumber: number = 0;
  message: string = '';
  uploadProgress: number = 0;
  uploadSuccess: boolean = false;

  onSelect(event: NgxDropzoneChangeEvent) {
    const addedImageFiles = event.addedFiles.filter(file => this.isImageFile(file));
    this.files.push(...addedImageFiles);

    const nonImageFiles = event.addedFiles.filter(file => !this.isImageFile(file));
    if (nonImageFiles.length > 0) {
      this._snackBar.open('Non-image files selected', 'Close', {
        panelClass: ['error-snackbar']
      });
    }
  }

  isImageFile(file: File): boolean {
    const allowedExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.bmp'];
    const fileExtension = file.name.toLowerCase().slice((file.name.lastIndexOf('.') - 1 >>> 0) + 2);
    return allowedExtensions.includes('.' + fileExtension);
  }

  onRemove(event: any) {
    this.files.splice(this.files.indexOf(event), 1);
  }

  ngOnInit() {
    this.getImages();
  }

  onUpload() {
    this.uploadProgress = 0;
    if (this.files.length === 0) {
      this._snackBar.open('No files selected', 'Close', {
        panelClass: ['error-snackbar']
      });
      this.uploadSuccess = false;
      return;
    }

    const uploadImageData = new FormData();
    this.files.forEach(file => {
      uploadImageData.append('images', file, file.name);
    });
    uploadImageData.append('galleryType', GalleryType.GLOBAL);
    this.imageService.uploadImages(uploadImageData)
      .subscribe((event) => {
        if (event.type === HttpEventType.UploadProgress) {
          this.uploadProgress = Math.round((event.loaded / event.total) * 100);
        }else if (event.type === HttpEventType.Response) {
          if (event.status === 200) {
            this._snackBar.open('Images uploaded successfully', 'Close', {
              panelClass: ['success-snackbar']
            });
            this.files = [];
            this.ngOnInit();
            this.uploadSuccess = true;
          } else if (event.status === 415) {
            this._snackBar.open('One or more files are not data', 'Close', {
              panelClass: ['error-snackbar']
            });
            this.uploadSuccess = false;
          } else {
            this._snackBar.open('Images not uploaded successfully', 'Close', {
              panelClass: ['error-snackbar']
            });
            this.uploadSuccess = false;
          }
        }
      });
  }

  getImages(): void{
    let params: any = {};
    params['pageSize'] = 8;
    params['pageNumber'] = this.pageNumber;
    params['galleryType'] = GalleryType.GLOBAL;

    this.imageService.getImages(params)
      .subscribe(
        response => {
          this.images = response['content']
        }
      )
  }

  onSearchInputChange(newValue: string) {
    let params: any = {};
    params['keyword'] = newValue;
    params['galleryType'] = GalleryType.GLOBAL;
    params['pageSize'] = 8;
    params['pageNumber'] = this.pageNumber;
    if (this.searchKeyword === '') {
      this.getImages()
    } else {
      this.imageService.searchImages(params)
        .subscribe(
          response => {
            this.images = response['content']
          }
        )
    }
  }

  renderPage(event: number) {
    this.pageNumber = event;
    this.getImages();
  }


  onImageRemove(image: ImageMetadata) {
    const params = new HttpParams()
      .set('id', image.id)
      .set('url', image.url);

    this.imageService.deleteImage(params)
      .subscribe({
        complete: () => {
          this.images = this.images.filter(img => img.id !== image.id);
          this._snackBar.open('Image deleted successfully', 'Close', {
            panelClass: ['success-snackbar']
          });
        },
        error: (error) => {
          console.log("Error deleting image", error)
        }
      });

  }
}
