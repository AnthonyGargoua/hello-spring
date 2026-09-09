package fr.diginamic.hello.config;

import org.hibernate.dialect.MariaDBDialect;

/**
 * Hibernate 7's MariaDBDialect always emits "alter table if exists ..." even though
 * that syntax only exists starting with MariaDB 10.5.2. This override disables it so
 * the generated DDL stays compatible with older MariaDB servers.
 */
public class LegacyMariaDBDialect extends MariaDBDialect {

	@Override
	public boolean supportsIfExistsAfterAlterTable() {
		return false;
	}
}
