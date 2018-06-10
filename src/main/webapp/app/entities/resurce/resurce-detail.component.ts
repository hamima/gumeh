import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs/Subscription';
import { JhiEventManager } from 'ng-jhipster';

import { Resurce } from './resurce.model';
import { ResurceService } from './resurce.service';

@Component({
    selector: 'jhi-resurce-detail',
    templateUrl: './resurce-detail.component.html'
})
export class ResurceDetailComponent implements OnInit, OnDestroy {

    resurce: Resurce;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private resurceService: ResurceService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInResurces();
    }

    load(id) {
        this.resurceService.find(id)
            .subscribe((resurceResponse: HttpResponse<Resurce>) => {
                this.resurce = resurceResponse.body;
            });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInResurces() {
        this.eventSubscriber = this.eventManager.subscribe(
            'resurceListModification',
            (response) => this.load(this.resurce.id)
        );
    }
}
