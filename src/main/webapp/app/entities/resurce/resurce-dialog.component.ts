import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';

import { Observable } from 'rxjs/Observable';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { Resurce } from './resurce.model';
import { ResurcePopupService } from './resurce-popup.service';
import { ResurceService } from './resurce.service';

@Component({
    selector: 'jhi-resurce-dialog',
    templateUrl: './resurce-dialog.component.html'
})
export class ResurceDialogComponent implements OnInit {

    resurce: Resurce;
    isSaving: boolean;

    constructor(
        public activeModal: NgbActiveModal,
        private resurceService: ResurceService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.resurce.id !== undefined) {
            this.subscribeToSaveResponse(
                this.resurceService.update(this.resurce));
        } else {
            this.subscribeToSaveResponse(
                this.resurceService.create(this.resurce));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<Resurce>>) {
        result.subscribe((res: HttpResponse<Resurce>) =>
            this.onSaveSuccess(res.body), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess(result: Resurce) {
        this.eventManager.broadcast({ name: 'resurceListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError() {
        this.isSaving = false;
    }
}

@Component({
    selector: 'jhi-resurce-popup',
    template: ''
})
export class ResurcePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private resurcePopupService: ResurcePopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.resurcePopupService
                    .open(ResurceDialogComponent as Component, params['id']);
            } else {
                this.resurcePopupService
                    .open(ResurceDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
