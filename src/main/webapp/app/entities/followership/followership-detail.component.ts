import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs/Subscription';
import { JhiEventManager } from 'ng-jhipster';

import { Followership } from './followership.model';
import { FollowershipService } from './followership.service';

@Component({
    selector: 'jhi-followership-detail',
    templateUrl: './followership-detail.component.html'
})
export class FollowershipDetailComponent implements OnInit, OnDestroy {

    followership: Followership;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private followershipService: FollowershipService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInFollowerships();
    }

    load(id) {
        this.followershipService.find(id)
            .subscribe((followershipResponse: HttpResponse<Followership>) => {
                this.followership = followershipResponse.body;
            });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInFollowerships() {
        this.eventSubscriber = this.eventManager.subscribe(
            'followershipListModification',
            (response) => this.load(this.followership.id)
        );
    }
}
