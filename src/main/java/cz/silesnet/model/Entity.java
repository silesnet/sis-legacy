package cz.silesnet.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;

/**
 * Base model class implementing toString(), equals(), hash() methods using
 * apache-commons-lang library. Also including Id for persistence purposes.
 *
 * @author Richard Sikora
 */
public abstract class Entity implements Serializable {

  // ~ Static fields/initializers
  // ---------------------------------------------

  private static final long serialVersionUID = 2005228878467135340L;

  // ~ Instance fields
  // --------------------------------------------------------

  private Long id;

  // ~ Methods
  // ----------------------------------------------------------------

  public void setId(Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  public boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }

  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  public String toString() {
    return ToStringBuilder.reflectionToString(this,
        ToStringStyle.MULTI_LINE_STYLE);
  }
}