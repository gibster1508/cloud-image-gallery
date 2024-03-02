import { HttpClient, HttpEventType } from '@angular/common/http';
import { Component } from '@angular/core';
import {NgxDropzoneChangeEvent} from "ngx-dropzone";
import {ImageService} from "../../service/image-service";
import {ImageMetadata} from "../../domain/ImageMetadata";

@Component({
  selector: 'app-image-gallery',
  templateUrl: './image-gallery.component.html',
  styleUrl: './image-gallery.component.scss'
})
export class ImageGalleryComponent {
  constructor(private httpClient: HttpClient,
              private imageService: ImageService) {}
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
      this.message ='Non-image files selected:';
    }
  }

  isImageFile(file: File): boolean {
    const allowedExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.bmp'];
    const fileExtension = file.name.toLowerCase().slice((file.name.lastIndexOf('.') - 1 >>> 0) + 2);
    return allowedExtensions.includes('.' + fileExtension);
  }

  onRemove(event: any) {
    console.log(event);
    this.files.splice(this.files.indexOf(event), 1);
  }

  ngOnInit() {
    this.getImages();
  }

  onUpload() {
    this.uploadProgress = 0;
    console.log(this.files);
    if (this.files.length === 0) {
      this.message = 'No files selected';
      this.uploadSuccess = false;
      return;
    }

    const uploadImageData = new FormData();
    this.files.forEach(file => {
      uploadImageData.append('images', file, file.name);
    });

    this.imageService.uploadImages(uploadImageData)
      .subscribe((event) => {
        if (event.type === HttpEventType.UploadProgress) {
          this.uploadProgress = Math.round((event.loaded / event.total) * 100);
        }else if (event.type === HttpEventType.Response) {
          if (event.status === 200) {
            this.message = 'Images uploaded successfully';
            this.files = [];
            this.ngOnInit();
            this.uploadSuccess = true;
          } else if (event.status === 415) {
            this.message = 'One or more files are not data';
            this.uploadSuccess = false;
          } else {
            this.message = 'Images not uploaded successfully';
            this.uploadSuccess = false;
          }
        }
        console.log(this.message);
      });
  }

  getImages(): void{
    let params: any = {};
    params['pageSize'] = 8;
    params['pageNumber'] = this.pageNumber;

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
}
