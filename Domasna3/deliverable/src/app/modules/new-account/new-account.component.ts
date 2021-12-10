import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthenticationService } from 'src/app/services/authentication.service';

@Component({
  selector: 'app-new-account',
  templateUrl: './new-account.component.html',
  styleUrls: ['./new-account.component.scss']
})
export class NewAccountComponent implements OnInit {

  token: any;
  newAccountForm!: FormGroup;
  loading = false;
  submitted = false;
  error = '';
  success = '';

  constructor(
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private authcenticationService: AuthenticationService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.token = params['token'];
      console.log(this.token);
  });
    this.newAccountForm = this.formBuilder.group({
      password: ['', Validators.required],
      repeatPassword: ['', Validators.required],
    })
  }

  get f() {
    return this.newAccountForm.controls;
  }

  onSubmit(): void {
    this.error = '';
    this.success = '';
    this.submitted = true;

    if(this.newAccountForm.invalid){
      return;
    }

    this.loading = true;
    this.authcenticationService
      .newAccount(this.token, this.f.password.value, this.f.repeatPassword.value)
      .subscribe(
        (data) => {
          console.log(data)
          this.success = data.message;
          this.loading = false;
        },
        (error) => {
          console.log(error)
          this.error = error.error.message;
          this.loading = false;
        }
      );

  }

}
