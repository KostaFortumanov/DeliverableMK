import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { AuthenticationService } from 'src/app/services/authentication.service';

@Component({
  selector: 'app-register-form',
  templateUrl: './register-form.component.html',
  styleUrls: ['./register-form.component.scss']
})
export class RegisterFormComponent implements OnInit {
  registerForm!: FormGroup;
  loading = false;
  submitted = false;
  error = '';
  success = '';

  constructor(
    private formBuilder: FormBuilder,
    private authcenticationService: AuthenticationService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.registerForm = this.formBuilder.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', Validators.required],
      phoneNumber: ['', Validators.required],
      role: ['', Validators.required]
    })
  }

  get f() {
    return this.registerForm.controls;
  }

  onSubmit(): void {
    this.error = '';
    this.success = '';
    this.submitted = true;
    
    if(this.registerForm.invalid){
      return;
    }

    this.loading = true;
    this.authcenticationService
      .register(this.f.firstName.value, this.f.lastName.value, this.f.email.value, this.f.phoneNumber.value, this.f.role.value)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe(
        (data) => {
          console.log(data)
          this.success = data.message;
        },
        (error) => {
          console.log(error)
          this.error = error.error.message;
        }
      );
  }

  back() {
    this.router.navigate(['/'])
  }
}
