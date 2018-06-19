import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { Followership } from './followership.model';
import { FollowershipPopupService } from './followership-popup.service';
import { FollowershipService } from './followership.service';

@Component({
    selector: 'jhi-followership-delete-dialog',
    templateUrl: './followership-delete-dialog.component.html'
})
export class FollowershipDeleteDialogComponent {

    followership: Followership;

    constructor(
        private followershipService: FollowershipService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.followershipService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'followershipListModification',
                content: 'Deleted an followership'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-followership-delete-popup',
    template: ''
})
export class FollowershipDeletePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private followershipPopupService: FollowershipPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.followershipPopupService
                .open(FollowershipDeleteDialogComponent as Component, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
