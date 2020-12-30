/*************************************************************
 Copyright 2018-2019 eBay Inc.
 Author/Developer: Jianwu Chen

 Use of this source code is governed by an MIT-style
 license that can be found in the LICENSE file or at
 https://opensource.org/licenses/MIT.
 ************************************************************/

package org.jsonex.jsoncoder.coder;

import org.jsonex.core.factory.InjectableInstance;
import org.jsonex.jsoncoder.BeanCoderContext;
import org.jsonex.jsoncoder.BeanCoderException;
import org.jsonex.jsoncoder.ICoder;
import org.jsonex.jsoncoder.JSONCoderOption;
import org.jsonex.treedoc.TDNode;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Date;

public class CoderDate implements ICoder<Date> {
  public static final InjectableInstance<CoderDate> it = InjectableInstance.of(CoderDate.class);
  public static CoderDate get() { return it.get(); }

  @Override public Class<Date> getType() { return Date.class; }
  
  @Override public TDNode encode(Date obj, Type type, BeanCoderContext ctx, TDNode target) {
    String dateFormat = ctx.getOption().getDateFormat();
    return target.setValue(dateFormat == null ? obj.getTime() : ctx.getCachedDateFormat(dateFormat).format(obj));
  }

  @Override public Date decode(TDNode jsonNode, Type type, Object targetObj, BeanCoderContext ctx) { return _decode(jsonNode.getValue(), ctx); }
  
  static Date _decode(Object obj, BeanCoderContext ctx) {
    if (obj instanceof Number)
      return new Date(((Number) obj).longValue());

    JSONCoderOption opt = ctx.getOption();
    String dateStr = (String) obj;
    try {
      return ctx.getCachedDateFormat(opt.getDateFormat()).parse(dateStr);
    } catch(ParseException e) {
      try { 
        return opt.parseDateFullback(dateStr);
      } catch (ParseException e1) {
        throw new BeanCoderException(e1);
      }
    }
  }
}