package entity.interfaces.user;

import entity.Filter;

public interface Filterable {
    Filter getFilter();
    void setFilter(Filter filter);
}