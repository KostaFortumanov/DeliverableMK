import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatOptionModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { RouterModule } from '@angular/router';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { DragDropModule } from '@angular/cdk/drag-drop';

import { SharedModule } from 'src/app/shared/shared.module';
import { DefaultComponent } from './default.component';
import { DashboardComponent } from 'src/app/modules/dashboard/dashboard.component';
import { DashboardService } from 'src/app/services/dashboard.service';
import { AddjobComponent } from 'src/app/modules/addjob/addjob.component';
import { AllDriversComponent, EditUserDialog } from 'src/app/modules/all-drivers/all-drivers.component';
import { SelectDriversComponent } from 'src/app/modules/select-drivers/select-drivers.component';
import { DriverMapComponent } from 'src/app/modules/driver-map/driver-map.component';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule } from '@angular/material/dialog'
import { ManagerJobsComponent } from 'src/app/modules/manager-jobs/manager-jobs.component';
import { MatTabsModule } from '@angular/material/tabs'
import { MatExpansionModule } from '@angular/material/expansion'

@NgModule({
  declarations: [
    DefaultComponent,
    DashboardComponent,
    DriverMapComponent,
    AddjobComponent,
    AllDriversComponent,
    SelectDriversComponent,
    EditUserDialog,
    ManagerJobsComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    SharedModule,
    MatSidenavModule,
    MatDividerModule,
    FlexLayoutModule,
    MatTableModule,
    MatPaginatorModule,
    FormsModule,
    MatFormFieldModule,
    MatButtonModule,
    MatAutocompleteModule,
    MatOptionModule,
    MatInputModule,
    MatProgressSpinnerModule,
    ReactiveFormsModule,
    MatCardModule,
    DragDropModule,
    MatIconModule,
    MatDialogModule,
    MatTabsModule,
    MatExpansionModule
  ],
  providers: [DashboardService],
})
export class DefaultModule {}
