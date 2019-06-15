import React from "react"
import './Search.css'

export const Search = ({filter}) => {
  return (
      <div className="search">
        <SearchIcons/>

        <form noValidate="noValidate" className="search-form">
          <input type="search" placeholder="Search..." required="required" className="search-input"
                 onChange={event => filter(event.target.value)}/>
          <span className="search-submit">
          <svg role="img" aria-label="Search">
            <use xlinkHref="#search-icon-magnifier"/>
          </svg>
        </span>
          <button type="reset" className="search-reset" onClick={() => filter("")}>
            <svg role="img" aria-label="Reset">
              <use xlinkHref="#search-icon-cross"/>
            </svg>
          </button>
        </form>
      </div>
  )
}

const SearchIcons = () => {
  return (
      <svg xmlns="http://www.w3.org/2000/svg" style={{display: "none"}}>
        <symbol xmlns="http://www.w3.org/2000/svg" id="search-icon-magnifier" viewBox="0 0 40 41">
          <path
              d="M26.51 28.573c-2.803 2.34-6.412 3.748-10.35 3.748C7.236 32.32 0 25.087 0 16.16 0 7.236 7.235 0 16.16 0c8.926 0 16.16 7.235 16.16 16.16 0 4.213-1.61 8.048-4.25 10.925L40 39.015l-1.524 1.524L26.51 28.572zm-10.35 2.132c8.033 0 14.545-6.512 14.545-14.544S24.193 1.617 16.16 1.617 1.617 8.128 1.617 16.16c0 8.033 6.512 14.545 14.545 14.545z"
              fillRule="evenodd"/>
        </symbol>
        <symbol xmlns="http://www.w3.org/2000/svg" id="search-icon-cross" viewBox="0 0 20 20">
          <path
              d="M8.96 10L.52 1.562 0 1.042 1.04 0l.522.52L10 8.96 18.438.52l.52-.52L20 1.04l-.52.522L11.04 10l8.44 8.438.52.52L18.96 20l-.522-.52L10 11.04l-8.438 8.44-.52.52L0 18.96l.52-.522L8.96 10z"
              fillRule="evenodd"/>
        </symbol>
      </svg>
  )
}
