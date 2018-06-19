import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { SERVER_API_URL } from '../../app.constants';

import { JhiDateUtils } from 'ng-jhipster';

import { Followership } from './followership.model';
import { createRequestOption } from '../../shared';

export type EntityResponseType = HttpResponse<Followership>;

@Injectable()
export class FollowershipService {

    private resourceUrl =  SERVER_API_URL + 'api/followerships';
    private resourceSearchUrl = SERVER_API_URL + 'api/_search/followerships';

    constructor(private http: HttpClient, private dateUtils: JhiDateUtils) { }

    create(followership: Followership): Observable<EntityResponseType> {
        const copy = this.convert(followership);
        return this.http.post<Followership>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    update(followership: Followership): Observable<EntityResponseType> {
        const copy = this.convert(followership);
        return this.http.put<Followership>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<Followership>(`${this.resourceUrl}/${id}`, { observe: 'response'})
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    query(req?: any): Observable<HttpResponse<Followership[]>> {
        const options = createRequestOption(req);
        return this.http.get<Followership[]>(this.resourceUrl, { params: options, observe: 'response' })
            .map((res: HttpResponse<Followership[]>) => this.convertArrayResponse(res));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response'});
    }

    search(req?: any): Observable<HttpResponse<Followership[]>> {
        const options = createRequestOption(req);
        return this.http.get<Followership[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
            .map((res: HttpResponse<Followership[]>) => this.convertArrayResponse(res));
    }

    private convertResponse(res: EntityResponseType): EntityResponseType {
        const body: Followership = this.convertItemFromServer(res.body);
        return res.clone({body});
    }

    private convertArrayResponse(res: HttpResponse<Followership[]>): HttpResponse<Followership[]> {
        const jsonResponse: Followership[] = res.body;
        const body: Followership[] = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            body.push(this.convertItemFromServer(jsonResponse[i]));
        }
        return res.clone({body});
    }

    /**
     * Convert a returned JSON object to Followership.
     */
    private convertItemFromServer(followership: Followership): Followership {
        const copy: Followership = Object.assign({}, followership);
        copy.createdAt = this.dateUtils
            .convertDateTimeFromServer(followership.createdAt);
        return copy;
    }

    /**
     * Convert a Followership to a JSON which can be sent to the server.
     */
    private convert(followership: Followership): Followership {
        const copy: Followership = Object.assign({}, followership);

        copy.createdAt = this.dateUtils.toDate(followership.createdAt);
        return copy;
    }
}
