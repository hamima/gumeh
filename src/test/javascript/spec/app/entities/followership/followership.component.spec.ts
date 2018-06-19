/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { Observable } from 'rxjs/Observable';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { GumehTestModule } from '../../../test.module';
import { FollowershipComponent } from '../../../../../../main/webapp/app/entities/followership/followership.component';
import { FollowershipService } from '../../../../../../main/webapp/app/entities/followership/followership.service';
import { Followership } from '../../../../../../main/webapp/app/entities/followership/followership.model';

describe('Component Tests', () => {

    describe('Followership Management Component', () => {
        let comp: FollowershipComponent;
        let fixture: ComponentFixture<FollowershipComponent>;
        let service: FollowershipService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GumehTestModule],
                declarations: [FollowershipComponent],
                providers: [
                    FollowershipService
                ]
            })
            .overrideTemplate(FollowershipComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(FollowershipComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(FollowershipService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN
                const headers = new HttpHeaders().append('link', 'link;link');
                spyOn(service, 'query').and.returnValue(Observable.of(new HttpResponse({
                    body: [new Followership(123)],
                    headers
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.query).toHaveBeenCalled();
                expect(comp.followerships[0]).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
