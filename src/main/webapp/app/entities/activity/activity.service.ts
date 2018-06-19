import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { SERVER_API_URL } from '../../app.constants';

import { JhiDateUtils } from 'ng-jhipster';

import { Activity } from './activity.model';
import { createRequestOption } from '../../shared';

export type EntityResponseType = HttpResponse<Activity>;

@Injectable()
export class ActivityService {

    private resourceUrl =  SERVER_API_URL + 'api/activities';
    private resourceSearchUrl = SERVER_API_URL + 'api/_search/activities';

    constructor(private http: HttpClient, private dateUtils: JhiDateUtils) { }

    create(activity: Activity): Observable<EntityResponseType> {
        const copy = this.convert(activity);
        return this.http.post<Activity>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    update(activity: Activity): Observable<EntityResponseType> {
        const copy = this.convert(activity);
        return this.http.put<Activity>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<Activity>(`${this.resourceUrl}/${id}`, { observe: 'response'})
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    query(req?: any): Observable<HttpResponse<Activity[]>> {
        const options = createRequestOption(req);
        return this.http.get<Activity[]>(this.resourceUrl, { params: options, observe: 'response' })
            .map((res: HttpResponse<Activity[]>) => this.convertArrayResponse(res));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response'});
    }

    search(req?: any): Observable<HttpResponse<Activity[]>> {
        const options = createRequestOption(req);
        return this.http.get<Activity[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
            .map((res: HttpResponse<Activity[]>) => this.convertArrayResponse(res));
    }

    private convertResponse(res: EntityResponseType): EntityResponseType {
        const body: Activity = this.convertItemFromServer(res.body);
        return res.clone({body});
    }

    private convertArrayResponse(res: HttpResponse<Activity[]>): HttpResponse<Activity[]> {
        const jsonResponse: Activity[] = res.body;
        const body: Activity[] = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            body.push(this.convertItemFromServer(jsonResponse[i]));
        }
        return res.clone({body});
    }

    /**
     * Convert a returned JSON object to Activity.
     */
    private convertItemFromServer(activity: Activity): Activity {
        const copy: Activity = Object.assign({}, activity);
        copy.createdAt = this.dateUtils
            .convertDateTimeFromServer(activity.createdAt);
        return copy;
    }

    /**
     * Convert a Activity to a JSON which can be sent to the server.
     */
    private convert(activity: Activity): Activity {
        const copy: Activity = Object.assign({}, activity);

        copy.createdAt = this.dateUtils.toDate(activity.createdAt);
        return copy;
    }
}
