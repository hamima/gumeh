import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { SERVER_API_URL } from '../../app.constants';

import { Resurce } from './resurce.model';
import { createRequestOption } from '../../shared';

export type EntityResponseType = HttpResponse<Resurce>;

@Injectable()
export class ResurceService {

    private resourceUrl =  SERVER_API_URL + 'api/resurces';
    private resourceSearchUrl = SERVER_API_URL + 'api/_search/resurces';

    constructor(private http: HttpClient) { }

    create(resurce: Resurce): Observable<EntityResponseType> {
        const copy = this.convert(resurce);
        return this.http.post<Resurce>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    update(resurce: Resurce): Observable<EntityResponseType> {
        const copy = this.convert(resurce);
        return this.http.put<Resurce>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<Resurce>(`${this.resourceUrl}/${id}`, { observe: 'response'})
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    query(req?: any): Observable<HttpResponse<Resurce[]>> {
        const options = createRequestOption(req);
        return this.http.get<Resurce[]>(this.resourceUrl, { params: options, observe: 'response' })
            .map((res: HttpResponse<Resurce[]>) => this.convertArrayResponse(res));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response'});
    }

    search(req?: any): Observable<HttpResponse<Resurce[]>> {
        const options = createRequestOption(req);
        return this.http.get<Resurce[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
            .map((res: HttpResponse<Resurce[]>) => this.convertArrayResponse(res));
    }

    private convertResponse(res: EntityResponseType): EntityResponseType {
        const body: Resurce = this.convertItemFromServer(res.body);
        return res.clone({body});
    }

    private convertArrayResponse(res: HttpResponse<Resurce[]>): HttpResponse<Resurce[]> {
        const jsonResponse: Resurce[] = res.body;
        const body: Resurce[] = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            body.push(this.convertItemFromServer(jsonResponse[i]));
        }
        return res.clone({body});
    }

    /**
     * Convert a returned JSON object to Resurce.
     */
    private convertItemFromServer(resurce: Resurce): Resurce {
        const copy: Resurce = Object.assign({}, resurce);
        return copy;
    }

    /**
     * Convert a Resurce to a JSON which can be sent to the server.
     */
    private convert(resurce: Resurce): Resurce {
        const copy: Resurce = Object.assign({}, resurce);
        return copy;
    }
}
