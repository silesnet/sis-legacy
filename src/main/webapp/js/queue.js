var q = {};
(function(q) {
  var subs = {},
    subUid = -1;

  function isFunction(func) {
    return func && {}.toString.call(func) === "[object Function]";
  }

  q.on = function(event, el, func) {
    if (isFunction(event)) {
      func = event;
      event = "*";
      el = undefined;
    } else if (isFunction(el)) {
      func = el;
      el = undefined;
    } else if (isFunction(func)) {
    } else {
      console.error("call back function not provided");
    }

    if (!subs[event]) {
      subs[event] = [];
    }

    if (el) {
      el.addEventListener(event, func);
      func = el.dispatchEvent.bind(el);
    }

    var token = ++subUid;
    subs[event].push({
      token: token,
      func: func
    });

    return token;
  };

  q.pub = function(event, args) {
    if (!subs[event]) {
      return false;
    }
    setTimeout(() => {
      var subscribers = subs[event].concat(subs["*"]);
      var len = subscribers ? subscribers.length : 0;
      while (len--) {
        var sub = subscribers[len];
        if (sub) {
          sub.func(new CustomEvent(event, { detail: args }));
        }
      }
    }, 0);
    return true;
  };

  q.off = function(token) {
    for (var s in subs) {
      if (subs[s]) {
        for (var i = 0, j = subs[s].length; i < j; i++) {
          if (subs[s][i].token === token) {
            subs[s].splice(i, 1);
            return token;
          }
        }
      }
    }
    return false;
  };
})(q);
