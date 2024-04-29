import React from "react";
import { ReactComponent as archive } from "./img/history.svg";
import { ReactComponent as single } from "./img/single.svg";
import { ReactComponent as multi } from "./img/multi.svg";
import { ReactComponent as add } from "./img/add.svg";
import { ReactComponent as SVGshare } from "./img/share.svg";
import { ReactComponent as SVGremove } from "./img/remove.svg";
import { ReactComponent as SVGdone } from "./img/done.svg";
import { ReactComponent as SVGflag } from "./img/flag.svg";

const ToggleView = ({ isThreeColumns, onClick }) => (
  <button className="toggle" onClick={onClick}>
    {isThreeColumns ? <SVGMulti /> : <SVGSingle />}
  </button>
);

const SVGarchive = ({ onClick }) => <button onClick={onClick}><SVGarchive /> History</button>;
const SVGShare = ({ onClick }) => <button onClick={onClick}><SVGshare /> Share</button>;
const SVGAdd = ({ onClick }) => <button onClick={onClick}><SVGAdd /> New Task</button>;
const Remove = ({ onClick }) => <button onClick={onClick}><SVGremove /> Remove</button>;
const Done = ({ onClick }) => <button onClick={onClick}><SVGdone /> Done</button>;
const Flag = ({ onClick }) => <button onClick={onClick}><SVGflag /> Skip to Next Day</button>;

export default { ToggleView, SVGarchive, SVGShare, SVGAdd, Remove, Done, Flag };
