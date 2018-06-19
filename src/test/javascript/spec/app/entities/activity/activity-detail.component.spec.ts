/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { GumehTestModule } from '../../../test.module';
import { ActivityDetailComponent } from '../../../../../../main/webapp/app/entities/activity/activity-detail.component';
import { ActivityService } from '../../../../../../main/webapp/app/entities/activity/activity.service';
import { Activity } from '../../../../../../main/webapp/app/entities/activity/activity.model';

describe('Component Tests', () => {

    describe('Activity Management Detail Component', () => {
        let comp: ActivityDetailComponent;
        let fixture: ComponentFixture<ActivityDetailComponent>;
        let service: ActivityService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GumehTestModule],
                declarations: [ActivityDetailComponent],
                providers: [
                    ActivityService
                ]
            })
            .overrideTemplate(ActivityDetailComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(ActivityDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(ActivityService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                spyOn(service, 'find').and.returnValue(Observable.of(new HttpResponse({
                    body: new Activity(123)
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.find).toHaveBeenCalledWith(123);
                expect(comp.activity).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
