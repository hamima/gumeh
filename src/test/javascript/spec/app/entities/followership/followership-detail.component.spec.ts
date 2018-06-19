/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { GumehTestModule } from '../../../test.module';
import { FollowershipDetailComponent } from '../../../../../../main/webapp/app/entities/followership/followership-detail.component';
import { FollowershipService } from '../../../../../../main/webapp/app/entities/followership/followership.service';
import { Followership } from '../../../../../../main/webapp/app/entities/followership/followership.model';

describe('Component Tests', () => {

    describe('Followership Management Detail Component', () => {
        let comp: FollowershipDetailComponent;
        let fixture: ComponentFixture<FollowershipDetailComponent>;
        let service: FollowershipService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GumehTestModule],
                declarations: [FollowershipDetailComponent],
                providers: [
                    FollowershipService
                ]
            })
            .overrideTemplate(FollowershipDetailComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(FollowershipDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(FollowershipService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                spyOn(service, 'find').and.returnValue(Observable.of(new HttpResponse({
                    body: new Followership(123)
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.find).toHaveBeenCalledWith(123);
                expect(comp.followership).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
