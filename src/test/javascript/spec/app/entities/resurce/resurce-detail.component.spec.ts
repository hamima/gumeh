/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { GumehTestModule } from '../../../test.module';
import { ResurceDetailComponent } from '../../../../../../main/webapp/app/entities/resurce/resurce-detail.component';
import { ResurceService } from '../../../../../../main/webapp/app/entities/resurce/resurce.service';
import { Resurce } from '../../../../../../main/webapp/app/entities/resurce/resurce.model';

describe('Component Tests', () => {

    describe('Resurce Management Detail Component', () => {
        let comp: ResurceDetailComponent;
        let fixture: ComponentFixture<ResurceDetailComponent>;
        let service: ResurceService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GumehTestModule],
                declarations: [ResurceDetailComponent],
                providers: [
                    ResurceService
                ]
            })
            .overrideTemplate(ResurceDetailComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(ResurceDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(ResurceService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                spyOn(service, 'find').and.returnValue(Observable.of(new HttpResponse({
                    body: new Resurce(123)
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.find).toHaveBeenCalledWith(123);
                expect(comp.resurce).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
