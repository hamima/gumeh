/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { Observable } from 'rxjs/Observable';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { GumehTestModule } from '../../../test.module';
import { ResurceComponent } from '../../../../../../main/webapp/app/entities/resurce/resurce.component';
import { ResurceService } from '../../../../../../main/webapp/app/entities/resurce/resurce.service';
import { Resurce } from '../../../../../../main/webapp/app/entities/resurce/resurce.model';

describe('Component Tests', () => {

    describe('Resurce Management Component', () => {
        let comp: ResurceComponent;
        let fixture: ComponentFixture<ResurceComponent>;
        let service: ResurceService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GumehTestModule],
                declarations: [ResurceComponent],
                providers: [
                    ResurceService
                ]
            })
            .overrideTemplate(ResurceComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(ResurceComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(ResurceService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN
                const headers = new HttpHeaders().append('link', 'link;link');
                spyOn(service, 'query').and.returnValue(Observable.of(new HttpResponse({
                    body: [new Resurce(123)],
                    headers
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.query).toHaveBeenCalled();
                expect(comp.resurces[0]).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
