import { Component, OnInit } from '@angular/core';
import {
  CdkDragDrop,
  moveItemInArray,
  transferArrayItem,
} from '@angular/cdk/drag-drop';

@Component({
  selector: 'app-select-drivers',
  templateUrl: './select-drivers.component.html',
  styleUrls: ['./select-drivers.component.scss'],
})
export class SelectDriversComponent implements OnInit {
  constructor() {}

  ngOnInit(): void {}

  drivers = [
    'Костадин Фортуманов',
    'Никола Ценевски',
    'Станко Станчевски',
    'Петар Петровски',
    'Стефан Стефанов',
    'Никола Ценевски',
    'Станко Станчевски',
    'Петар Петровски',
  ];

  selected = ['Станко Станчевски'];

  submit() {
    console.log(this.selected);
  }

  drop(event: CdkDragDrop<string[]>) {
    if (event.previousContainer === event.container) {
      moveItemInArray(
        event.container.data,
        event.previousIndex,
        event.currentIndex
      );
    } else {
      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex
      );
    }
  }
}
