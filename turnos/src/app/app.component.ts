import {HttpClient} from '@angular/common/http';
import {Component, ViewChild, AfterViewInit, Inject, OnInit} from '@angular/core';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {merge, Observable, of as observableOf} from 'rxjs';
import {catchError, map, startWith, switchMap} from 'rxjs/operators';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import {MatSnackBar} from '@angular/material/snack-bar';
import { FormControl, FormGroup, Validators } from '@angular/forms';

const print = (turno: Turno) => {
  
  var printWindow = window.open();
  printWindow.document.open('text/plain')
  printWindow.document.write(`
      ^XA        
      ^FX${turno.name} ${turno.lastname} ZPL^FS
      ^FO50,100
      ^A0N,89^FD${turno.date} ${turno.time}^FS
      ^A0N,89^FD${turno.place}^FS
      ^XZ
  `);
  printWindow.document.close();
  printWindow.focus();
  printWindow.print();
  printWindow.close();
}

@Component({
  selector: 'app-root',
  styleUrls: ['app.component.css'],
  templateUrl: 'app.component.html',
})
export class TurnosTable implements AfterViewInit {
  displayedColumns: string[] = ['hc', 'name', 'lastname', 'time', 'place', 'date', 'actions'];
  exampleDatabase: HttpDatabase | null;
  data: Turno[] = [];

  resultsLength = 0;
  isLoadingResults = true;
  isEmpty = false;
  isFiltered = false;

  filterValue: string;

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  constructor(private _httpClient: HttpClient, public _dialog: MatDialog) {}

  printZpl(turno: Turno): void{
    print(turno);
  }

  openTurno(): void {
    const dialogRef = this._dialog.open(NewTurnoDialog, {
      width: '500px',
      data: {}
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result){
        setTimeout(() => this.refresh(), 500)
      }
    })

  }

  refresh(): void{
    merge(this.sort.sortChange, this.paginator.page)
    .pipe(
      startWith({}),
      switchMap(() => {
        this.isLoadingResults = true;
        return this.exampleDatabase!.getTurnos(
          this.sort.active, this.sort.direction, this.paginator.pageIndex, this.filterValue);
      }),
      map(data => {
        // Flip flag to show that loading has finished.
        this.isLoadingResults = false;
        this.resultsLength = data.length;
        this.isEmpty = data.length == 0;

        return data;
      }),
      catchError(err => {
        console.log('Catched Error', err);
        
        this.isLoadingResults = false;
        this.isEmpty = true;
        // Catch if the GitHub API has reached its rate limit. Return empty data.
        return observableOf([]);
      })
    ).subscribe(data => this.data = data);
  }

  applyFilter(event: Event) {
    this.filterValue = (event.target as HTMLInputElement).value;
    this.isFiltered = true;
    this.refresh();
  }

  ngAfterViewInit() {
    this.exampleDatabase = new HttpDatabase(this._httpClient);

    // If the user changes the sort order, reset back to the first page.
    this.sort.sortChange.subscribe(() => this.paginator.pageIndex = 0);
    this.refresh();
  }
}

@Component({
  selector: 'dialog-overview-example-dialog',
  templateUrl: 'new-turno-dialog.html',
})
export class NewTurnoDialog implements OnInit, AfterViewInit {

  exampleDatabase: HttpDatabase | null;
  turnoGroup: FormGroup;
 
  constructor(
    private _dialogRef: MatDialogRef<NewTurnoDialog>, private _snackBar: MatSnackBar, private _httpClient: HttpClient,
    @Inject(MAT_DIALOG_DATA) public data: Turno) {

    _dialogRef.afterClosed().subscribe(result => {
      if (result){
        let turno: Turno = {
          name: this.turnoGroup.get('name').value,
          lastname: this.turnoGroup.get('name').value,
          hc: this.turnoGroup.get('hc').value,
          place: this.turnoGroup.get('place').value,
          date: this.turnoGroup.get('date').value,
          time: this.turnoGroup.get('time').value
        };
        return this.addTurno(turno)
      }
    });

    }

  onNoClick(): void {
    this._dialogRef.close();
  }

  ngAfterViewInit(){
    this.exampleDatabase = new HttpDatabase(this._httpClient);
  }

  showErrorSnack(error: ErrorType): void {
    this._snackBar.open(error.message || error.text || 'Error', 'Cerrar', {
      duration: 3000
    });
  }

  addTurno(turno: Turno) {
    this.exampleDatabase.saveTurno(turno.hc, turno.name, turno.lastname, turno.date, turno.time, turno.place)
    .pipe(
      catchError(err => {
        console.log('Save Turno Error', err);
        this.showErrorSnack(err)
        return observableOf([]);
      })
    )
    .subscribe(result => {
      const turno = result as Turno;
      console.log('add trnoldkad', turno);
      this.showSuccessSnack();
      print(turno);
    });
  }

  showSuccessSnack(): void {
    this._snackBar.open('El turno ha sido otorgado correctamente', 'Cerrar', {
      duration: 3000
    });
  }

  ngOnInit(){
    this.turnoGroup = new FormGroup({
      name: new FormControl('', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]),
      lastname: new FormControl('', [Validators.required, Validators.maxLength(100)]),
      hc: new FormControl('', [Validators.required, Validators.maxLength(15)]),
      date: new FormControl('', Validators.required),
      time: new FormControl('', Validators.required),
      place: new FormControl('', [Validators.required, Validators.maxLength(50)])
    });
  }

}

export interface Turno {
  name: string;
  lastname: string;
  hc: number;
  date: Date;
  time: string;
  place: string;
}

export interface ErrorType {
  message: string;
  text: string;
}

/** An example database that the data source uses to retrieve data for the table. */
export class HttpDatabase {
  constructor(private _httpClient: HttpClient) {}

  getTurnos(sort: string, order: string, page: number, text: string): Observable<Turno[]> {
    const href = 'http://localhost:8080/turnos/search';
    let requestUrl =
        `${href}?sort=${sort}&order=${order}&page=${page + 1}`;

    if (text){
      requestUrl+=`&text=${text}`
    }

    return this._httpClient.get<Turno[]>(requestUrl);
  }

  saveTurno(hc: number, name: string, lastname: string, date: Date, time: string, place: string): Observable<Turno> {
    const requestUrl = 'http://localhost:8080/turnos';
    const body = {
      hc, name, lastname, date, time, place
    };
    return this._httpClient.post<Turno>(requestUrl, body);
  }
}