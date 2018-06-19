import { BaseEntity } from './../../shared';

export class Followership implements BaseEntity {
    constructor(
        public id?: number,
        public createdAt?: any,
        public reason?: string,
        public comment?: string,
        public userLogin?: string,
        public userId?: number,
        public tagTitle?: string,
        public tagId?: number,
    ) {
    }
}
