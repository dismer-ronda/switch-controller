package es.pryades.smartswitch.common;

import es.pryades.smartswitch.dto.Query;

/**
 * 
 * @author Dismer Ronda
 *
 */
public interface EnermetPaginatorFilter extends TablePaginator
{
	public Query getPaginatorQuery();
	public void setPaginatorQuery(Query query);
}
