import { BaseEntity } from './../../shared';

export const enum ResourceType {
    'Poet',
    'Novel',
    'Book',
    'Song',
    'Site',
    'Movie',
    'Person'
}

export class Resurce implements BaseEntity {
    constructor(
        public id?: number,
        public type?: ResourceType,
        public title?: string,
        public publishYear?: number,
        public noPosts?: number,
        public creator?: string,
        public link?: string,
        public activated?: boolean,
        public certified?: boolean,
    ) {
        this.activated = false;
        this.certified = false;
    }
}
