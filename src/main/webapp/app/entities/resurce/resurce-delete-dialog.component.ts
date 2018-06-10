import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { Resurce } from './resurce.model';
import { ResurcePopupService } from './resurce-popup.service';
import { ResurceService } from './resurce.service';

@Component({
    selector: 'jhi-resurce-delete-dialog',
    templateUrl: './resurce-delete-dialog.component.html'
})
export class ResurceDeleteDialogComponent {

    resurce: Resurce;

    constructor(
        private resurceService: ResurceService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.resurceService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'resurceListModification',
                content: 'Deleted an resurce'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-resurce-delete-popup',
    template: ''
})
export class ResurceDeletePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private resurcePopupService: ResurcePopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.resurcePopupService
                .open(ResurceDeleteDialogComponent as Component, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
