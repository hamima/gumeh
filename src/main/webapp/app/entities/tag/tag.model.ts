import { BaseEntity } from './../../shared';

export class Tag implements BaseEntity {
    constructor(
        public id?: number,
        public title?: string,
        public noPosts?: number,
        public noFollowers?: number,
        public relatedTags?: string,
        public encyclopediaLink?: string,
    ) {
    }
}
