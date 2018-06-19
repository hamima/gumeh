import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';

import { Observable } from 'rxjs/Observable';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { Followership } from './followership.model';
import { FollowershipPopupService } from './followership-popup.service';
import { FollowershipService } from './followership.service';
import { User, UserService } from '../../shared';
import { Tag, TagService } from '../tag';

@Component({
    selector: 'jhi-followership-dialog',
    templateUrl: './followership-dialog.component.html'
})
export class FollowershipDialogComponent implements OnInit {

    followership: Followership;
    isSaving: boolean;

    users: User[];

    tags: Tag[];

    constructor(
        public activeModal: NgbActiveModal,
        private jhiAlertService: JhiAlertService,
        private followershipService: FollowershipService,
        private userService: UserService,
        private tagService: TagService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.userService.query()
            .subscribe((res: HttpResponse<User[]>) => { this.users = res.body; }, (res: HttpErrorResponse) => this.onError(res.message));
        this.tagService.query()
            .subscribe((res: HttpResponse<Tag[]>) => { this.tags = res.body; }, (res: HttpErrorResponse) => this.onError(res.message));
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.followership.id !== undefined) {
            this.subscribeToSaveResponse(
                this.followershipService.update(this.followership));
        } else {
            this.subscribeToSaveResponse(
                this.followershipService.create(this.followership));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<Followership>>) {
        result.subscribe((res: HttpResponse<Followership>) =>
            this.onSaveSuccess(res.body), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess(result: Followership) {
        this.eventManager.broadcast({ name: 'followershipListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError() {
        this.isSaving = false;
    }

    private onError(error: any) {
        this.jhiAlertService.error(error.message, null, null);
    }

    trackUserById(index: number, item: User) {
        return item.id;
    }

    trackTagById(index: number, item: Tag) {
        return item.id;
    }
}

@Component({
    selector: 'jhi-followership-popup',
    template: ''
})
export class FollowershipPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private followershipPopupService: FollowershipPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.followershipPopupService
                    .open(FollowershipDialogComponent as Component, params['id']);
            } else {
                this.followershipPopupService
                    .open(FollowershipDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
