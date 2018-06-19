/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { Observable } from 'rxjs/Observable';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { GumehTestModule } from '../../../test.module';
import { ActivityComponent } from '../../../../../../main/webapp/app/entities/activity/activity.component';
import { ActivityService } from '../../../../../../main/webapp/app/entities/activity/activity.service';
import { Activity } from '../../../../../../main/webapp/app/entities/activity/activity.model';

describe('Component Tests', () => {

    describe('Activity Management Component', () => {
        let comp: ActivityComponent;
        let fixture: ComponentFixture<ActivityComponent>;
        let service: ActivityService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GumehTestModule],
                declarations: [ActivityComponent],
                providers: [
                    ActivityService
                ]
            })
            .overrideTemplate(ActivityComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(ActivityComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(ActivityService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN
                const headers = new HttpHeaders().append('link', 'link;link');
                spyOn(service, 'query').and.returnValue(Observable.of(new HttpResponse({
                    body: [new Activity(123)],
                    headers
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.query).toHaveBeenCalled();
                expect(comp.activities[0]).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
