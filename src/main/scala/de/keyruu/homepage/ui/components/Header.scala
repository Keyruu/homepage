package de.keyruu.homepage.ui.components

import scalatags.Text.all._

object Header:
  def apply(currentPath: String): Frag =
    header(
      tag("nav")(
        a(
          cls := "",
          href := "/",
          img(
            src := "/public/images/keyruu_logo.png",
            alt := "Keyruu",
            height := "60px"
          )
        ),
        div(
          cls := "internal-links",
          HeaderLink("/", "home", currentPath),
          HeaderLink("/blog", "blog", currentPath),
          HeaderLink("/about", "about", currentPath),
          HeaderLink("/projects", "projects", currentPath),
          HeaderLink("/contact", "contact", currentPath)
        )
      ),
      tag("style")("""
        me {
          position: sticky;
          top: 0;
          z-index: 999;
          backdrop-filter: blur(12px);
          -webkit-backdrop-filter: blur(12px);
          background: rgba(10, 10, 20, 0.7);
          border-bottom: 1px solid rgba(var(--gray), 0.2);
          padding: 0 1.5rem;
          box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
        }

        me nav {
          display: flex;
          align-items: center;
          justify-content: space-between;
          max-width: 1200px;
          margin: 0 auto;
          height: 64px;
        }

        me h2 {
          margin: 0;
          font-size: 1.25rem;
        }

        me h2 a {
          text-decoration: none;
          color: var(--accent);
          font-weight: bold;
          letter-spacing: 0.5px;
        }

        me .internal-links {
          display: flex;
          gap: 1rem;
        }

        me .internal-links a {
          padding: 0.5rem 0.25rem;
          font-weight: 500;
          color: rgba(220, 230, 245, 0.85);
          position: relative;
          text-decoration: none;
          transition: color 0.3s ease;
        }

        me .internal-links a::after {
          content: '';
          position: absolute;
          left: 0;
          bottom: 0px;
          width: 100%;
          height: 2px;
          background-color: var(--accent);
          transform: scaleX(0);
          transform-origin: right;
          transition: transform 0.4s ease, transform-origin 0s;
        }

        me .internal-links a:hover::after {
          transform: scaleX(1);
          transform-origin: left;
        }

        @media (max-width: 720px) {
          me nav {
            flex-direction: column;
            align-items: flex-start;
            height: auto;
            padding: 1rem 0;
          }

          me .internal-links {
            flex-wrap: wrap;
            gap: 0.75rem 1rem;
          }
        }
      """)
    )
