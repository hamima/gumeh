import { BaseEntity } from './../../shared';

export class Post implements BaseEntity {
    constructor(
        public id?: number,
        public resourceBrief?: string,
        public noLikes?: number,
        public tagStr?: string,
        public userBrief?: string,
        public link?: string,
        public text?: string,
        public categoryTitle?: string,
        public categoryId?: number,
    ) {
    }
}
