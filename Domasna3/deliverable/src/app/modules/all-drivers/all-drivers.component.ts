import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { finalize } from 'rxjs/operators';
import { AllDriverDetailsService } from 'src/app/services/all-driver-details.service';

export interface Driver {
  id: number;
  fullName: string;
  email: string;
  phone: string;
}


@Component({
  selector: 'app-all-drivers',
  templateUrl: './all-drivers.component.html',
  styleUrls: ['./all-drivers.component.scss'],
})
export class AllDriversComponent implements OnInit {

  show = false;
  pageLoading = true;
  error = '';

  displayedColumns: string[] = [
    'position',
    'name',
    'email',
    'phone',
    'actions',
  ];
  dataSource!: MatTableDataSource<Driver>;

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(
    private allDriverDetailsService: AllDriverDetailsService,
    public dialog: MatDialog,
    private formBuilder: FormBuilder,
    ) {}

  ngOnInit(): void {

    this.getDrivers();
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }

  edit(position: number) {
    console.log('edit: ' + position);
    let driver = this.dataSource.data[0];
    console.log(driver)
    this.openDialog(driver);
  }

  delete(id: number) {
    console.log(id);
    this.allDriverDetailsService.deleteDriver(id)
      .subscribe(
        (data) => {
          this.getDrivers();
        }
      )
  }

  name = 'kosta'
  animal = 'kuce';


  openDialog(driver: Driver): void {
    const dialogRef = this.dialog.open(EditUserDialog, {
      width: '500px',
      data: {id: driver.id, fullName: driver.fullName, email: driver.email, phone: driver.phone},
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
      this.getDrivers();
    });
  }

  getDrivers() {
    this.allDriverDetailsService.getDrivers()
    .pipe(
      finalize(() => {
        this.pageLoading = false;
        this.show = true;
      })
    )
      .subscribe(
      (data) => {
        this.dataSource = new MatTableDataSource<Driver>(data);
      },
      (error) => {
        this.error = 'Server unavailable';
      } 
    )
  }
}

@Component({
  selector: 'edit-user-dialog',
  templateUrl: 'edit-user-dialog.html',
})
export class EditUserDialog {
  constructor(
    public dialogRef: MatDialogRef<EditUserDialog>,
    @Inject(MAT_DIALOG_DATA) public data: Driver,
    private formBuilder: FormBuilder,
    private allDriverDetailsService: AllDriverDetailsService,
  ) {}

  onNoClick(): void {
    this.dialogRef.close();
  }

  editUserForm = this.formBuilder.group({
    email : this.data.email,
    phone : this.data.phone  
  }
  )

  onSubmit() {
    console.log(this.data.id)
    this.allDriverDetailsService.editDriver(this.data.id, this.editUserForm.get('email')!.value, this.editUserForm.get('phone')!.value)
      .subscribe(
        (data) => {
          this.onNoClick();
        
        }
      )
  }
}
