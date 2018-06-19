import { BaseEntity } from './../../shared';

export const enum ActivityType {
    'Like',
    'Comment',
    'Report',
    'Share'
}

export class Activity implements BaseEntity {
    constructor(
        public id?: number,
        public type?: ActivityType,
        public createdAt?: any,
        public comment?: string,
        public deleted?: boolean,
        public reportReason?: string,
        public userBrief?: string,
        public activityDesc?: string,
        public userLogin?: string,
        public userId?: number,
        public postId?: number,
    ) {
        this.deleted = false;
    }
}
