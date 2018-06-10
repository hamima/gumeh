/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs/Observable';
import { JhiEventManager } from 'ng-jhipster';

import { GumehTestModule } from '../../../test.module';
import { ResurceDeleteDialogComponent } from '../../../../../../main/webapp/app/entities/resurce/resurce-delete-dialog.component';
import { ResurceService } from '../../../../../../main/webapp/app/entities/resurce/resurce.service';

describe('Component Tests', () => {

    describe('Resurce Management Delete Component', () => {
        let comp: ResurceDeleteDialogComponent;
        let fixture: ComponentFixture<ResurceDeleteDialogComponent>;
        let service: ResurceService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [GumehTestModule],
                declarations: [ResurceDeleteDialogComponent],
                providers: [
                    ResurceService
                ]
            })
            .overrideTemplate(ResurceDeleteDialogComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(ResurceDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(ResurceService);
            mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
            mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
        });

        describe('confirmDelete', () => {
            it('Should call delete service on confirmDelete',
                inject([],
                    fakeAsync(() => {
                        // GIVEN
                        spyOn(service, 'delete').and.returnValue(Observable.of({}));

                        // WHEN
                        comp.confirmDelete(123);
                        tick();

                        // THEN
                        expect(service.delete).toHaveBeenCalledWith(123);
                        expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                        expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
                    })
                )
            );
        });
    });

});
